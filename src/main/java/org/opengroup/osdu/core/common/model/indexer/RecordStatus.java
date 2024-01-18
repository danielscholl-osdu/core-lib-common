// Copyright 2017-2019, Schlumberger
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

package org.opengroup.osdu.core.common.model.indexer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecordStatus {

    private String id;
    private String kind;
    private String operationType;

    private IndexingStatus status;

    private IndexProgress indexProgress;

    public String succeededAuditLogMessage() {
        return "RecordStatus(id=" + this.id + ", kind=" + this.kind + ", operationType=" + this.operationType + ", status=" + this.status.toString() + ")";
    }

    public String partiallySucceededAuditLogMessage() {
        return "RecordStatus(id=" + this.id + ", kind=" + this.kind + ", operationType=" + this.operationType + ", status==PARTIAL_SUCCESS" + this.status.toString() + ", message=" + this.getLatestTrace() + ")";
    }

    public String failedAuditLogMessage() {
        return "RecordStatus(id=" + this.id + ", kind=" + this.kind + ", operationType=" + this.operationType + ", status=" + this.status.toString() + ", message=" + this.getLatestTrace() + ")";
    }

    public String getLatestTrace() {
        if (indexProgress != null && indexProgress.getTrace() != null && indexProgress.getTrace().size() > 0) {
            return indexProgress.getTrace().peek();
        }
        return null;
    }
}