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

import javax.validation.constraints.NotEmpty;

import org.opengroup.osdu.core.common.model.storage.validation.ValidPatchOp;
import org.opengroup.osdu.core.common.model.storage.validation.ValidPatchPath;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ValidPatchOp
public class PatchOperation {
    @ApiModelProperty(value = SwaggerDoc.BULK_UPDATE_RECORD_OP,
            required = true,
            example = SwaggerDoc.BULK_UPDATE_RECORD_OP_EXAMPLE)
    private String op;

    @ApiModelProperty(value = SwaggerDoc.BULK_UPDATE_RECORD_PATH,
            required = true,
            example = SwaggerDoc.BULK_UPDATE_RECORD_PATH_EXAMPLE)
    @ValidPatchPath
    private String path;

    @ApiModelProperty(value = SwaggerDoc.BULK_UPDATE_RECORD_PATH,
            required = true)
    @NotEmpty
    private String[] value;
}
