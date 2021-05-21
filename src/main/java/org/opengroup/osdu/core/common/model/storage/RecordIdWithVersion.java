package org.opengroup.osdu.core.common.model.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordIdWithVersion {

    private String recordId;
    private Long recordVersion;

}
