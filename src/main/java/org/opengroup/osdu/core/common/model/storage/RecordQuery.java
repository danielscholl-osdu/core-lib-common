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

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import org.opengroup.osdu.core.common.model.storage.validation.ValidNotNullCollection;
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
public class RecordQuery {
    @ApiModelProperty(value = SwaggerDoc.FETCH_RECORD_ID_LIST,
            required = true,
            example = SwaggerDoc.RECORD_ID_EXAMPLE)
    @ValidNotNullCollection
    @NotEmpty(message = ValidationDoc.RECORD_ID_LIST_NOT_EMPTY)
    @Size(min = 1, max = 500, message = ValidationDoc.RECORDS_MAX)
    private List<String> ids;
}