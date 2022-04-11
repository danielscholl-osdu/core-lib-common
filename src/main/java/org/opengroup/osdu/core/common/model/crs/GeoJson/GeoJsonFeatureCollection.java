//Copyright 2021 Schlumberger
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

package org.opengroup.osdu.core.common.model.crs.GeoJson;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import org.opengroup.osdu.core.common.Constants;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel
public class GeoJsonFeatureCollection extends GeoJsonBase {
    @JsonProperty(Constants.FEATURES)
    private GeoJsonFeature[] features;

    @JsonProperty(Constants.PROPERTIES)
    private Object properties;

    @JsonProperty(Constants.PERSISTABLE_REFERENCE_CRS)
    private String persistableReferenceCrs;

    @JsonProperty(Constants.COORDINATE_REFERENCE_SYSTEM_ID)
    private String CoordinateReferenceSystemID;

    @JsonProperty(Constants.VERTICAL_UNIT_ID)
    private String VerticalUnitID;

    @JsonProperty(Constants.PERSISTABLE_REFERENCE_UNIT_Z)
    private String persistableReferenceUnitZ;

    public GeoJsonFeatureCollection() {
        super(Constants.ANY_CRS_FEATURE_COLLECTION);
    }
}
