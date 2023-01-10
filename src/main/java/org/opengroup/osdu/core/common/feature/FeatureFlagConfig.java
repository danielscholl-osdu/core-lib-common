package org.opengroup.osdu.core.common.feature;

import org.opengroup.osdu.core.common.logging.JaxRsDpsLog;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.partition.IPartitionFactory;
import org.opengroup.osdu.core.common.partition.IPartitionProvider;
import org.opengroup.osdu.core.common.partition.PartitionAPIConfig;
import org.opengroup.osdu.core.common.partition.PartitionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeatureFlagConfig {
    @Autowired
    private DpsHeaders headers;

    @Autowired
    private JaxRsDpsLog logger;

    IPartitionProvider partitionProvider;

    @Value("${PARTITION_API}")
    private String partitionAPIEndpoint;

    @Bean
    @ConditionalOnProperty(prefix = "featureFlag", name = "strategy", havingValue = "dataPartition")
    public PartitionFeatureFlagImpl dataPartitionFeatureFlag() {
        PartitionAPIConfig apiConfig = PartitionAPIConfig.builder()
                .rootUrl(partitionAPIEndpoint)
                .build();
        IPartitionFactory iPartitionFactory = new PartitionFactory(apiConfig);
        partitionProvider = iPartitionFactory.create(headers);
        return new PartitionFeatureFlagImpl(partitionProvider, logger, headers);
    }

    @Bean
    @ConditionalOnProperty(prefix = "featureFlag", name = "strategy", havingValue = "appProperty", matchIfMissing = true)
    public AppPropertiesFeatureFlagImpl environmentFeatureFlag() {
        return new AppPropertiesFeatureFlagImpl();
    }
}
