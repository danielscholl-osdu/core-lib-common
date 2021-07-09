package org.opengroup.osdu.core.common.status;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;

@RunWith(MockitoJUnitRunner.class)
public class AttributesBuilderTest {

    private static final String DATA_PARTITION_ID = "data-partition-id";
    private static final String CORRELATION_ID = "correlation-id";

    @Mock
    private DpsHeaders dpsHeaders;

    @InjectMocks
    private AttributesBuilder attributesBuilder;

    @Test
    public void testCreateAttributesMap() {
        when(dpsHeaders.getCorrelationId()).thenReturn(CORRELATION_ID);
        when(dpsHeaders.getPartitionId()).thenReturn(DATA_PARTITION_ID);
        
        Map<String, String> attributesMap = attributesBuilder.createAttributesMap();
        
        assertEquals(DATA_PARTITION_ID, attributesMap.get(DpsHeaders.DATA_PARTITION_ID));
        assertEquals(CORRELATION_ID, attributesMap.get(DpsHeaders.CORRELATION_ID));
    }
}
