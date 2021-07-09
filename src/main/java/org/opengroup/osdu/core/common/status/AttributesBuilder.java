package org.opengroup.osdu.core.common.status;

import java.util.HashMap;
import java.util.Map;

import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Lazy
@Component
@RequiredArgsConstructor
public class AttributesBuilder {

    private final DpsHeaders dpsHeaders;

    public Map<String, String> createAttributesMap() {
        Map<String, String> attributesMap = new HashMap<>();

        attributesMap.put(DpsHeaders.CORRELATION_ID, dpsHeaders.getCorrelationId());
        attributesMap.put(DpsHeaders.DATA_PARTITION_ID, dpsHeaders.getPartitionId());

        return attributesMap;
    }
}
