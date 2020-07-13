package org.opengroup.osdu.core.common.model.notification;

import lombok.Data;

@Data
public class GsaSecret extends Secret {
    private GsaSecretValue value;

    public GsaSecret() {
        super(Constants.GSAString);
    }
}