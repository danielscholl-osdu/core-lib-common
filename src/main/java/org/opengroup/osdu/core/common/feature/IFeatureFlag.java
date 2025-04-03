package org.opengroup.osdu.core.common.feature;

public interface IFeatureFlag {

  boolean isFeatureEnabled(String featureName);

  default boolean isFeatureEnabled(String featureName, String dataPartitionId) {
    return isFeatureEnabled(featureName);
  }

  default String source() {
    return "N/A";
  }
}
