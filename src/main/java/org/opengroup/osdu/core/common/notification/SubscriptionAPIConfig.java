package org.opengroup.osdu.core.common.notification;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SubscriptionAPIConfig {
    @Builder.Default
    String rootUrl = "https://os-register-dot-opendes.appspot.com/api/register/v1";

    String apiKey;

    public static SubscriptionAPIConfig Default() {
        return SubscriptionAPIConfig.builder().build();
    }
}
