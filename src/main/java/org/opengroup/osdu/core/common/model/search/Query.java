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

package org.opengroup.osdu.core.common.model.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.opengroup.osdu.core.common.SwaggerDoc;
import org.opengroup.osdu.core.common.model.search.validation.ValidMultiKind;
import org.opengroup.osdu.core.common.model.search.validation.ValidSortOrder;
import org.opengroup.osdu.core.common.model.search.validation.ValidSpatialFilter;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
public abstract class Query {

    @NotNull(message = SwaggerDoc.KIND_VALIDATION_CAN_NOT_BE_NULL_OR_EMPTY)
    @ApiModelProperty(value = SwaggerDoc.KIND_REQUEST_DESCRIPTION, required = true, example = SwaggerDoc.KIND_EXAMPLE)
    @ValidMultiKind
    private Object kind;

    @Min(value = 0, message = SwaggerDoc.LIMIT_VALIDATION_MIN_MSG)
    @ApiModelProperty(value = SwaggerDoc.LIMIT_DESCRIPTION, dataType = "java.lang.Integer", example = "30")
    private int limit;

    @ApiModelProperty(value = SwaggerDoc.QUERY_DESCRIPTION)
    private String query = "";

    @ApiModelProperty(value = SwaggerDoc.SUGGEST_DESCRIPTION)
    private String suggestPhrase = "";

    @JsonIgnore
    boolean returnHighlightedFields = false;

    @ApiModelProperty(value = SwaggerDoc.HIGHLIGHTED_FIELDS_DESCRIPTION)
    private List<String> highlightedFields = new ArrayList<>();

    @ApiModelProperty(value = SwaggerDoc.RETURNED_FIELDS_DESCRIPTION)
    private List<String> returnedFields = new ArrayList<>();

    @Valid
    @ValidSortOrder
    @ApiModelProperty(value = SwaggerDoc.SORT_DESCRIPTION)
    private SortQuery sort;

    @ApiModelProperty(value = SwaggerDoc.QUERYASOWNER_DESCRIPTION, dataType = "java.lang.Boolean", example = "false")
    private boolean queryAsOwner;

    @ApiModelProperty(value = SwaggerDoc.TRACKTOTALCOUNT_DESCRIPTION, dataType = "java.lang.Boolean", example = "false")
    private boolean trackTotalCount = false;

    @Valid
    @ValidSpatialFilter
    @ApiModelProperty(value = SwaggerDoc.SPATIAL_FILTER_DESCRIPTION)
    private SpatialFilter spatialFilter;
}
