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

    public static final String FF_SOURCE_DATA_PARTITION = "dataPartition";
    @Autowired
    private JaxRsDpsLog logger;
    @Autowired
    private DpsHeaders headers;
    @Autowired
    private IServiceAccountJwtClient tokenService;

    @Value("${PARTITION_API:not_used}")
    private String partitionAPIEndpoint;

    @Override
    public boolean isFeatureEnabled(String featureName) {
        IPartitionProvider partitionProvider = getPartitionProvider(headers);
        return fetchFeatureFlag(featureName, headers.getPartitionId(), partitionProvider);
    }

    @Override
    public boolean isFeatureEnabled(String featureName, String dataPartitionId) {
        DpsHeaders dpsHeaders = new DpsHeaders();
        dpsHeaders.put(DpsHeaders.DATA_PARTITION_ID, dataPartitionId);
        IPartitionProvider partitionProvider = getPartitionProvider(dpsHeaders);
        return fetchFeatureFlag(featureName, dataPartitionId, partitionProvider);
    }

    @Override
    public String source() {
        return FF_SOURCE_DATA_PARTITION;
    }

    private boolean fetchFeatureFlag(String featureName, String dataPartitionId, IPartitionProvider partitionProvider) {
        if (partitionProvider == null)
            return false;
        try {
            PartitionInfo partitionInfo = partitionProvider.get(dataPartitionId);
            return getFeatureFlagStatus(partitionInfo, featureName);
        } catch (PartitionException e) {
            this.logger.error(String.format("Error getting feature flag status for dataPartitionId: %s, exception http response: %s", headers.getPartitionId(), e.getResponse().toString()));
            //Partition returns 200, when data-partition id is null, so not a valid scenario to return the return code from partition service as is.
            //So, only handling 404 separately.
            int errorCode = e.getHttpResponse().getResponseCode() == HttpStatus.SC_NOT_FOUND ? HttpStatus.SC_NOT_FOUND: HttpStatus.SC_INTERNAL_SERVER_ERROR;

            StringBuilder errorMessage = new StringBuilder( String.format("Error from partition Service: %s", e.getMessage()));
            errorMessage.append(String.format("Response body: %s", e.getResponse().getBody()));

            throw new AppException(errorCode, String.format("Error getting feature flag value for property: %s, partition: %s",
                featureName, headers.getPartitionId()), errorMessage.toString(), e);
        }
    }

    private IPartitionProvider getPartitionProvider(DpsHeaders headers) {
        PartitionAPIConfig apiConfig = PartitionAPIConfig.builder()
            .rootUrl(partitionAPIEndpoint)
            .build();
        IPartitionFactory partitionFactory = new PartitionFactory(apiConfig);
        DpsHeaders partitionHeaders = DpsHeaders.createFromMap(headers.getHeaders());
        partitionHeaders.put(DpsHeaders.AUTHORIZATION, tokenService.getIdToken(headers.getPartitionId()));
        return partitionFactory.create(partitionHeaders);
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
