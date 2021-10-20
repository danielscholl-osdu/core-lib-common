// Copyright Schlumberger
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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.HttpStatus;
import org.opengroup.osdu.core.common.model.http.AppException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchemaInfo {

    private static final long serialVersionUID = 1L;

    private String kind;
    private String op;

    public static Map<String, SchemaOperationType> getCreateSchemaEvents(List<SchemaInfo> messages) {
        return getEvent(messages, SchemaOperationType.create);
    }

    public static Map<String, SchemaOperationType> getUpdateSchemaEvents(List<SchemaInfo> messages) {
        return getEvent(messages, SchemaOperationType.update);
    }

    private static Map<String, SchemaOperationType> getEvent(List<SchemaInfo> messages, SchemaOperationType operationType) {
        Map<String, SchemaOperationType> schemaOperations = new HashMap<>();

        try {
            for (SchemaInfo msg : messages) {
                SchemaOperationType op = SchemaOperationType.valueOf(msg.getOp());
                if (op == operationType) {
                    schemaOperations.put(msg.getKind(), op);
                }
            }
        } catch (Exception e) {
            throw new AppException(HttpStatus.SC_BAD_REQUEST, "Request parsing error", "Error parsing schema updates in request payload.", e);
        }
        return schemaOperations;
    }
}
