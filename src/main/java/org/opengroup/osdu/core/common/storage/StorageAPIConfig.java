package org.opengroup.osdu.core.common.storage;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StorageAPIConfig {

    String rootUrl;

    String apiKey;

    public static StorageAPIConfig Default() {
        return StorageAPIConfig.builder().build();
    }
}
