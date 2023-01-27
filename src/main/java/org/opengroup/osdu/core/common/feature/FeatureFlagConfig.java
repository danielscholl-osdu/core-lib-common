package org.opengroup.osdu.core.common.feature;

import org.opengroup.osdu.core.common.logging.JaxRsDpsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "featureFlag", name = "strategy")
public class FeatureFlagConfig {
    @Autowired
    private JaxRsDpsLog logger;

    @Bean
    @ConditionalOnProperty(prefix = "featureFlag", name = "strategy", havingValue = "dataPartition")
    public PartitionFeatureFlagImpl dataPartitionFeatureFlag() {
        return new PartitionFeatureFlagImpl(logger);
    }

    @Bean
    @ConditionalOnProperty(prefix = "featureFlag", name = "strategy", havingValue = "appProperty", matchIfMissing = true)
    public AppPropertiesFeatureFlagImpl environmentFeatureFlag() {
        return new AppPropertiesFeatureFlagImpl();
    }
}
