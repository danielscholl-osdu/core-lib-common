package org.opengroup.osdu.core.common.model.storage;

import lombok.Data;

import java.util.List;

@Data
public class UpsertRecords {
    private Integer recordCount;
    private List<String> recordIds;
    private List<String> skippedRecordIds;
}
