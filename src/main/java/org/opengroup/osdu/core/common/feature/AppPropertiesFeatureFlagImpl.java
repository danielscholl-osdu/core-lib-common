package org.opengroup.osdu.core.common.feature;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "featureFlag", name = "strategy", havingValue = "appProperty", matchIfMissing = true)
public class AppPropertiesFeatureFlagImpl implements IFeatureFlag {

    public static final String FF_SOURCE_APP_PROPERTY = "appProperty";
    @Autowired
    private Environment env;

    @Override
    public boolean isFeatureEnabled(String featureName) {
        return ("true").equals(env.getProperty(featureName));
    }

    @Override
    public String source() {
        return FF_SOURCE_APP_PROPERTY;
    }
}
