package org.opengroup.osdu.core.common.feature;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class AppPropertiesFeatureFlagImpl implements IFeatureFlag {

    @Autowired
    private Environment env;

    @Override
    public boolean isFeatureEnabled(String featureName) {
        return env.getProperty(featureName).equals("true");
    }

}
