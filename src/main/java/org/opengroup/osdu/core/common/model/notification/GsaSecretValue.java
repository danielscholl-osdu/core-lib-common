package org.opengroup.osdu.core.common.model.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GsaSecretValue {
    private String audience;
    private String key;
}