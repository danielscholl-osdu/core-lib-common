package org.opengroup.osdu.core.common.feature;

import org.opengroup.osdu.core.common.logging.JaxRsDpsLog;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.partition.IPartitionFactory;
import org.opengroup.osdu.core.common.partition.IPartitionProvider;
import org.opengroup.osdu.core.common.partition.PartitionAPIConfig;
import org.opengroup.osdu.core.common.partition.PartitionFactory;
import org.opengroup.osdu.core.common.util.IServiceAccountJwtClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(prefix = "featureFlag", name = "strategy")
public class FeatureFlagConfig {
    @Autowired
    private DpsHeaders headers;

    @Autowired
    private JaxRsDpsLog logger;

    IPartitionProvider partitionProvider;

    private IServiceAccountJwtClient tokenService;

    @Value("${PARTITION_API:not_used}")
    private String partitionAPIEndpoint;

    @Bean
    @ConditionalOnProperty(prefix = "featureFlag", name = "strategy", havingValue = "dataPartition")
    public PartitionFeatureFlagImpl dataPartitionFeatureFlag() {
        PartitionAPIConfig apiConfig = PartitionAPIConfig.builder()
                .rootUrl(partitionAPIEndpoint)
                .build();
        IPartitionFactory iPartitionFactory = new PartitionFactory(apiConfig);
        DpsHeaders partitionHeaders = DpsHeaders.createFromMap(headers.getHeaders());
        partitionHeaders.put(DpsHeaders.AUTHORIZATION, tokenService.getIdToken(headers.getPartitionId()));
        partitionProvider = iPartitionFactory.create(partitionHeaders);
        return new PartitionFeatureFlagImpl(partitionProvider, logger, partitionHeaders);
    }

    @Bean
    @ConditionalOnProperty(prefix = "featureFlag", name = "strategy", havingValue = "appProperty", matchIfMissing = true)
    public AppPropertiesFeatureFlagImpl environmentFeatureFlag() {
        return new AppPropertiesFeatureFlagImpl();
    }
}
