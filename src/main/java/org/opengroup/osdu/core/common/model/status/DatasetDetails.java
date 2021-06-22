package org.opengroup.osdu.core.common.model.status;

import lombok.Data;

@Data
public class DatasetDetails {

    private String kind;
    private Properties properties;

    @Data
    public class Properties {
        private String correlationId;
        private String datasetId;
        private String datasetVersionId;
        private DatasetType datasetType;
        private int recordCount;
        private String timestamp;
    }

}
