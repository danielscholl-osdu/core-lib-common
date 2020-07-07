package org.opengroup.osdu.core.common.model.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscriptionInfo {
    private String name = "";
    private String description = "";
    private String topic = "";
    private String pushEndpoint = "";
    private String notificationId = "";
    private String id = "";
    private String createdBy = "";

    public SubscriptionInfo(Subscription subscription) {
        this.name = subscription.getName();
        this.description = subscription.getDescription();
        this.topic = subscription.getTopic();
        this.pushEndpoint = subscription.getPushEndpoint();
        this.notificationId = subscription.getNotificationId();
        this.id = subscription.getId();
        this.createdBy = subscription.getCreatedBy();
    }
}
