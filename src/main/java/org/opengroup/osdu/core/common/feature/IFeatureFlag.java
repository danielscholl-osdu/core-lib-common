package org.opengroup.osdu.core.common.feature;

import org.opengroup.osdu.core.common.partition.PartitionException;

public interface IFeatureFlag {
    public boolean isFeatureEnabled(String featureName);
}
