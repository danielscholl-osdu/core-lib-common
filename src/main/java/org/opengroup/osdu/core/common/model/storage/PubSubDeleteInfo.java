package org.opengroup.osdu.core.common.model.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.opengroup.osdu.core.common.model.indexer.DeletionType;
import org.opengroup.osdu.core.common.model.indexer.OperationType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PubSubDeleteInfo extends PubSubInfo{

    private DeletionType deletionType;

    public PubSubDeleteInfo(String recordId, String kind, DeletionType deletionType) {
        super(recordId, kind, OperationType.delete);
        this.deletionType = deletionType;
    }
}
