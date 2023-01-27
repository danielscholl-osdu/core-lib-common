package org.opengroup.osdu.core.common.feature;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.http.HttpStatus;
import org.opengroup.osdu.core.common.logging.JaxRsDpsLog;
import org.opengroup.osdu.core.common.model.http.AppException;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.partition.IPartitionFactory;
import org.opengroup.osdu.core.common.partition.IPartitionProvider;
import org.opengroup.osdu.core.common.partition.PartitionAPIConfig;
import org.opengroup.osdu.core.common.partition.PartitionException;
import org.opengroup.osdu.core.common.partition.PartitionFactory;
import org.opengroup.osdu.core.common.partition.PartitionInfo;
import org.opengroup.osdu.core.common.util.IServiceAccountJwtClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "featureFlag", name = "strategy", havingValue = "dataPartition")
public class PartitionFeatureFlagImpl implements IFeatureFlag {
    private final JaxRsDpsLog logger;
    @Autowired
    private DpsHeaders headers;
    @Autowired
    private IServiceAccountJwtClient tokenService;

    @Value("${PARTITION_API:not_used}")
    private String partitionAPIEndpoint;

    public PartitionFeatureFlagImpl(JaxRsDpsLog logger) {
        this.logger = logger;
    }

    @Override
    public boolean isFeatureEnabled(String featureName) {
        PartitionAPIConfig apiConfig = PartitionAPIConfig.builder()
                .rootUrl(partitionAPIEndpoint)
                .build();
        IPartitionFactory partitionFactory = new PartitionFactory(apiConfig);
        DpsHeaders partitionHeaders = DpsHeaders.createFromMap(headers.getHeaders());
        partitionHeaders.put(DpsHeaders.AUTHORIZATION, tokenService.getIdToken(headers.getPartitionId()));
        IPartitionProvider partitionProvider = partitionFactory.create(partitionHeaders);

        if (partitionProvider == null)
            return false;
        try {
            PartitionInfo partitionInfo = partitionProvider.get(headers.getPartitionId());
            return getFeatureFlagStatus(partitionInfo, featureName);
        } catch (PartitionException e) {
            this.logger.error(String.format("Error getting feature flag status for dataPartitionId: %s, exception http response: %s", headers.getPartitionId(), e.getResponse().toString()));
            throw new AppException(HttpStatus.SC_INTERNAL_SERVER_ERROR, String.format("Error getting feature flag value for property %s, partition %s ", featureName, headers.getPartitionId()), e.getMessage(), e);
        }
    }

    private boolean getFeatureFlagStatus(PartitionInfo partitionInfo, String featureName) {
        final Gson gson = new Gson();
        JsonElement element = gson.toJsonTree(partitionInfo.getProperties());
        JsonObject rootObject = element.getAsJsonObject();
        if (!rootObject.has(featureName)) {
            return false;
        }

        return rootObject.getAsJsonObject(featureName).get("value").getAsBoolean();
    }
}
