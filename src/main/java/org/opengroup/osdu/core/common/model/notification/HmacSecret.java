package org.opengroup.osdu.core.common.model.notification;

import lombok.Data;

@Data
public class HmacSecret extends Secret {
    private String value;

    public HmacSecret() {
        super(Constants.HMACString);
    }
}