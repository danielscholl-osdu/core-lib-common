// Copyright 2021 Schlumberger
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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
