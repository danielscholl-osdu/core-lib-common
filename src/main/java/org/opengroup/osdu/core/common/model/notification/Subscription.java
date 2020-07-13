package org.opengroup.osdu.core.common.model.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Subscription {
    private String name = "";
    private String description = "";
    private String topic = "";
    private String pushEndpoint = "";
    private String notificationId = "";
    private String id = "";
    private String createdBy = "";
    private Secret secret;
}