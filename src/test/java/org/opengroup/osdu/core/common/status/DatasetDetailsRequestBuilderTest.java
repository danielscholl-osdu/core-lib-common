package org.opengroup.osdu.core.common.status;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.opengroup.osdu.core.common.logging.JaxRsDpsLog;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.model.status.DatasetDetails;
import org.opengroup.osdu.core.common.model.status.DatasetType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class DatasetDetailsRequestBuilderTest {

    private static final String DATA_PARTITION_ID = "data-partition-id";
    private static final String CORRELATION_ID = "correlation-id";
    private static final int RECORD_COUNT_1 = 1;
    private static final String DATASET_VERSION_ID = "v1";
    private static final String DATASET_ID = "abc123";
    private static final String DATASET_DETAILS = "datasetDetails";

    @Mock
    private DpsHeaders dpsHeaders;

    @Spy
    private ObjectMapper mapper = new ObjectMapper();

    @Mock
    private JaxRsDpsLog log;

    @InjectMocks
    private DatasetDetailsRequestBuilder requestBuilder;

    @Before
    public void setup() {
        when(dpsHeaders.getCorrelationId()).thenReturn(CORRELATION_ID);
    }

    @Test
    public void testBuildDatasetDetails() {
        DatasetDetails datasetDetails = requestBuilder.buildDatasetDetails(DATASET_ID, DatasetType.FILE,
                DATASET_VERSION_ID, RECORD_COUNT_1);

        assertNotNull(datasetDetails);
        assertNotNull(datasetDetails.getProperties());
        assertEquals(DATASET_DETAILS, datasetDetails.getKind());
        assertEquals(DATASET_ID, datasetDetails.getProperties().getDatasetId());
        assertEquals(DatasetType.FILE, datasetDetails.getProperties().getDatasetType());
        assertEquals(DATASET_VERSION_ID, datasetDetails.getProperties().getDatasetVersionId());
        assertEquals(RECORD_COUNT_1, datasetDetails.getProperties().getRecordCount());
    }

    @Test
    public void testCreateDatasetDetailsMessage() throws JsonProcessingException {
        String datasetDetailsAsString = requestBuilder.createDatasetDetailsMessage(DATASET_ID, DatasetType.FILE,
                DATASET_ID, RECORD_COUNT_1);

        assertNotNull(datasetDetailsAsString);
        assertFalse(datasetDetailsAsString.isEmpty());
    }

    @Test
    public void testCreateAttributesMap() {
        when(dpsHeaders.getPartitionId()).thenReturn(DATA_PARTITION_ID);

        Map<String, String> attributesMap = requestBuilder.createAttributesMap();

        assertNotNull(attributesMap);
        assertEquals(2, attributesMap.size());
        assertEquals(DATA_PARTITION_ID, attributesMap.get(DpsHeaders.DATA_PARTITION_ID));
        assertEquals(CORRELATION_ID, attributesMap.get(DpsHeaders.CORRELATION_ID));
    }
}
