package org.opengroup.osdu.core.common.feature;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.partition.IPartitionProvider;
import org.opengroup.osdu.core.common.partition.PartitionException;
import org.opengroup.osdu.core.common.partition.PartitionInfo;

@RequiredArgsConstructor
public class PartitionFeatureFlagImpl implements IFeatureFlag {
    private final IPartitionProvider partitionProvider;
    private final DpsHeaders dpsHeaders;

    @Override
    public boolean isFeatureEnabled(String featureName) throws PartitionException{
        if (partitionProvider == null)
            return false;

        PartitionInfo partitionInfo = partitionProvider.get(dpsHeaders.getPartitionId());
        return getFeatureFlagStatus(partitionInfo, featureName);
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
