package org.opengroup.osdu.core.common.status;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.opengroup.osdu.core.common.logging.JaxRsDpsLog;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.model.status.Status;
import org.opengroup.osdu.core.common.model.status.StatusDetails;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class StatusDetailsRequestBuilderTest {

    private static final String DATA_PARTITION_ID = "data-partition-id";
    private static final String CORRELATION_ID = "correlation-id";
    private static final String STATUS = "status";
    private static final String STAGE = "STORAGE_SYNC";
    private static final String MSG = "message";
    private static final int BAD_REQUEST_ERROR_CODE = HttpStatus.SC_BAD_REQUEST;
    private static final String RECORD_ID = "partition1:abc:1234";

    @Mock
    private DpsHeaders dpsHeaders;

    @Spy
    private ObjectMapper mapper = new ObjectMapper();

    @Mock
    private JaxRsDpsLog log;

    @InjectMocks
    private StatusDetailsRequestBuilder requestBuilder;

    @Before
    public void setup() {
        when(dpsHeaders.getCorrelationId()).thenReturn(CORRELATION_ID);
    }

    @Test
    public void testBuildDatasetDetails() {
        StatusDetails statusDetails = requestBuilder.buildStatusDetails(MSG, RECORD_ID, Status.FAILED, STAGE, BAD_REQUEST_ERROR_CODE);

        assertNotNull(statusDetails);
        assertNotNull(statusDetails.getProperties());
        assertEquals(STATUS, statusDetails.getKind());
        assertEquals(MSG, statusDetails.getProperties().getMessage());
        assertEquals(RECORD_ID, statusDetails.getProperties().getRecordId());
        assertEquals(Status.FAILED, statusDetails.getProperties().getStatus());
        assertEquals(STAGE, statusDetails.getProperties().getStage());
        assertEquals(BAD_REQUEST_ERROR_CODE, statusDetails.getProperties().getErrorCode());
    }

    @Test
    public void testCreateDatasetDetailsMessage() throws JsonProcessingException {
        String statusDetailsAsString = requestBuilder.createStatusDetailsMessage(MSG, RECORD_ID, Status.FAILED, STAGE, BAD_REQUEST_ERROR_CODE);

        assertNotNull(statusDetailsAsString);
        assertFalse(statusDetailsAsString.isEmpty());
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