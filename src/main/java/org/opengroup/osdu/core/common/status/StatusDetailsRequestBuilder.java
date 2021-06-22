package org.opengroup.osdu.core.common.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.model.status.Status;
import org.opengroup.osdu.core.common.model.status.StatusDetails;
import org.opengroup.osdu.core.common.model.status.StatusDetails.Properties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Lazy
@Component
@RequiredArgsConstructor
public class StatusDetailsRequestBuilder {

    private static final String KIND = "status";

    private final DpsHeaders dpsHeaders;
    private final ObjectMapper mapper;

    public StatusDetails buildStatusDetails(String msg, String recordId, Status status, String stage, int errorCode) {
        Properties properties = buildStatusProperties(msg, recordId, status, stage, errorCode);
        StatusDetails statusDetails = new StatusDetails();
        statusDetails.setKind(KIND);
        statusDetails.setProperties(properties);
        return statusDetails;
    }

    public String createStatusDetailsMessage(String msg, String recordId, Status status, String stage, int errorCode)
            throws JsonProcessingException {
        List<StatusDetails> statusDetailsList = new ArrayList<>();
        StatusDetails statusDetails = buildStatusDetails(msg, recordId, status, stage, errorCode);
        statusDetailsList.add(statusDetails);

        return mapper.writeValueAsString(statusDetailsList);
    }

    public Map<String, String> createAttributesMap() {
        Map<String, String> attributesMap = new HashMap<>();

        attributesMap.put(DpsHeaders.CORRELATION_ID, dpsHeaders.getCorrelationId());
        attributesMap.put(DpsHeaders.DATA_PARTITION_ID, dpsHeaders.getPartitionId());

        return attributesMap;
    }

    private Properties buildStatusProperties(String msg, String recordId, Status status, String stage, int errorCode) {
        StatusDetails statusDetails = new StatusDetails();
        Properties properties = statusDetails.new Properties();
        properties.setCorrelationId(dpsHeaders.getCorrelationId());
        properties.setErrorCode(errorCode);
        properties.setMessage(msg);
        properties.setRecordId(recordId);
        properties.setStage(stage);
        properties.setStatus(status);
        properties.setTimestamp(DateTime.now().toString());
        properties.setUserEmail(dpsHeaders.getUserEmail());
        return properties;
    }
}
