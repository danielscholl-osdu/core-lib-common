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

package org.opengroup.osdu.core.common.model.storage;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.opengroup.osdu.core.common.model.storage.validation.ValidBulkQuery;
import org.opengroup.osdu.core.common.model.storage.validation.ValidationDoc;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecordBulkUpdateParam {
    @ApiModelProperty(value = SwaggerDoc.RECORD_QUERY_CONDITION, required = true)
    @NotNull(message = ValidationDoc.RECORD_QUERY_CONDITION_NOT_EMPTY)
    @ValidBulkQuery
    private RecordQuery query;

    @ApiModelProperty(value = SwaggerDoc.RECORD_METADATA_OPERATIONS, required = true)
    @NotNull(message = ValidationDoc.RECORD_METADATA_OPERATIONS_NOT_EMPTY)
    @Valid
    private List<PatchOperation> ops;
}
