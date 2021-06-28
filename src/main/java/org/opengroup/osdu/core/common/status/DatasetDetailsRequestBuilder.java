package org.opengroup.osdu.core.common.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.model.status.DatasetDetails;
import org.opengroup.osdu.core.common.model.status.DatasetDetails.Properties;
import org.opengroup.osdu.core.common.model.status.DatasetType;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Lazy
@Component
@RequiredArgsConstructor
public class DatasetDetailsRequestBuilder {

    private static final String KIND = "datasetDetails";

    private final DpsHeaders dpsHeaders;
    private final ObjectMapper mapper;

    public DatasetDetails buildDatasetDetails(String datasetId, DatasetType datasetType, String datasetVersionId,
            int recordCount) {
        Properties properties = buildDatasetDetailsProperties(datasetId, datasetType, datasetVersionId, recordCount);
        DatasetDetails datasetDetails = new DatasetDetails();
        datasetDetails.setKind(KIND);
        datasetDetails.setProperties(properties);
        return datasetDetails;
    }

    public String createDatasetDetailsMessage(String datasetId, DatasetType datasetType, String datasetVersionId,
            int recordCount) throws JsonProcessingException {
        List<DatasetDetails> statusDetailsList = new ArrayList<>();
        DatasetDetails datasetDetails = buildDatasetDetails(datasetId, datasetType, datasetVersionId, recordCount);
        statusDetailsList.add(datasetDetails);

        return mapper.writeValueAsString(statusDetailsList);
    }

    public Map<String, String> createAttributesMap() {
        Map<String, String> attributesMap = new HashMap<>();

        attributesMap.put(DpsHeaders.CORRELATION_ID, dpsHeaders.getCorrelationId());
        attributesMap.put(DpsHeaders.DATA_PARTITION_ID, dpsHeaders.getPartitionId());

        return attributesMap;
    }

    private Properties buildDatasetDetailsProperties(String datasetId, DatasetType datasetType, String datasetVersionId,
            int recordCount) {
        DatasetDetails datasetDetails = new DatasetDetails();
        Properties properties = datasetDetails.new Properties();
        properties.setCorrelationId(dpsHeaders.getCorrelationId());
        properties.setDatasetId(datasetId);
        properties.setDatasetType(datasetType);
        properties.setDatasetVersionId(datasetVersionId);
        properties.setRecordCount(recordCount);
        properties.setTimestamp(DateTime.now().toString());
        return properties;
    }
}
