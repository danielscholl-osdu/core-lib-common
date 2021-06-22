package org.opengroup.osdu.core.common.model.status;

import lombok.Data;

@Data
public class StatusDetails {

    private String kind;
    private Properties properties;

    @Data
    public class Properties {

        private String correlationId;
        private String recordId;
        private String recordIdVersion;
        private String stage;
        private Status status;
        private String message;
        private int errorCode;
        private String userEmail;
        private String timestamp;

    }
}
