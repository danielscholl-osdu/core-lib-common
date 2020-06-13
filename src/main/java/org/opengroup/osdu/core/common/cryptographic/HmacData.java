package org.opengroup.osdu.core.common.cryptographic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HmacData {
    private String expireMillisecond;
    private String hashMechanism;
    private String endpointUrl;
    private String nonce;
}