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

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@EqualsAndHashCode(callSuper = true)
public class GeoJsonMultiPolygon extends GeoJsonBase {
    @JsonProperty("coordinates")
    private double[][][][] coordinates;

    public GeoJsonMultiPolygon() {
        super("AnyCrsMultiPolygon");
    } // default to non-GeoJSON
}
