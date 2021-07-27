// Copyright 2021 Schlumberger
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

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = GeoJsonPoint.class, name = "Point"),
        @JsonSubTypes.Type(value = GeoJsonPoint.class, name = "AnyCrsPoint"),
        @JsonSubTypes.Type(value = GeoJsonMultiPoint.class, name = "MultiPoint"),
        @JsonSubTypes.Type(value = GeoJsonMultiPoint.class, name = "AnyCrsMultiPoint"),
        @JsonSubTypes.Type(value = GeoJsonPolygon.class, name = "Polygon"),
        @JsonSubTypes.Type(value = GeoJsonPolygon.class, name = "AnyCrsPolygon"),
        @JsonSubTypes.Type(value = GeoJsonMultiPolygon.class, name = "MultiPolygon"),
        @JsonSubTypes.Type(value = GeoJsonMultiPolygon.class, name = "AnyCrsMultiPolygon"),
        @JsonSubTypes.Type(value = GeoJsonFeature.class, name = "Feature"),
        @JsonSubTypes.Type(value = GeoJsonFeature.class, name = "AnyCrsFeature"),
        @JsonSubTypes.Type(value = GeoJsonFeatureCollection.class, name = "FeatureCollection"),
        @JsonSubTypes.Type(value = GeoJsonFeatureCollection.class, name = "AnyCrsFeatureCollection")
})
@JsonIgnoreProperties({"valid", "dimension", "length", "geoJsonVariant"})
public abstract class GeoJsonBase {
    @JsonProperty("type")
    private String type;

    @JsonProperty("bbox")
    private double[] bbox;

    private int dimension;
    boolean valid;

    GeoJsonBase(String type) {
        this.type = type;
        this.dimension = -1;
        this.valid = false;
    }

    public int getDimension() {
        this.isValid();
        return this.dimension;
    }
}
