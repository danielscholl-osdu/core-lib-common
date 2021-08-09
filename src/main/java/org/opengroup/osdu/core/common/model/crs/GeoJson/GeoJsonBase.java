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
import lombok.EqualsAndHashCode;
import org.opengroup.osdu.core.common.Constants;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = GeoJsonPoint.class, name = Constants.POINT),
        @JsonSubTypes.Type(value = GeoJsonPoint.class, name = Constants.ANY_CRS_POINT),
        @JsonSubTypes.Type(value = GeoJsonMultiPoint.class, name = Constants.MULTIPOINT),
        @JsonSubTypes.Type(value = GeoJsonMultiPoint.class, name = Constants.ANY_CRS_MULTIPOINT),
        @JsonSubTypes.Type(value = GeoJsonLineString.class, name = Constants.LINE_STRING),
        @JsonSubTypes.Type(value = GeoJsonLineString.class, name = Constants.ANY_CRS_LINE_STRING),
        @JsonSubTypes.Type(value = GeoJsonMultiLineString.class, name = Constants.MULTI_LINE_STRING),
        @JsonSubTypes.Type(value = GeoJsonMultiLineString.class, name = Constants.ANY_CRS_MULTILINE_STRING),
        @JsonSubTypes.Type(value = GeoJsonPolygon.class, name = Constants.POLYGON),
        @JsonSubTypes.Type(value = GeoJsonPolygon.class, name = Constants.ANY_CRS_POLYGON),
        @JsonSubTypes.Type(value = GeoJsonMultiPolygon.class, name = Constants.MULTIPOLYGON),
        @JsonSubTypes.Type(value = GeoJsonMultiPolygon.class, name = Constants.ANY_CRS_MULTIPOLYGON),
        @JsonSubTypes.Type(value = GeoJsonGeometryCollection.class, name = Constants.GEOMETRYCOLLECTION),
        @JsonSubTypes.Type(value = GeoJsonGeometryCollection.class, name = Constants.ANY_CRS_GEOMETRY_COLLECTION),
        @JsonSubTypes.Type(value = GeoJsonFeature.class, name = Constants.FEATURE),
        @JsonSubTypes.Type(value = GeoJsonFeature.class, name = Constants.ANY_CRS_FEATURE),
        @JsonSubTypes.Type(value = GeoJsonFeatureCollection.class, name = Constants.FEATURE_COLLECTION),
        @JsonSubTypes.Type(value = GeoJsonFeatureCollection.class, name = Constants.ANY_CRS_FEATURE_COLLECTION)
})
@JsonIgnoreProperties({"valid", "dimension", "length", "geoJsonVariant"})
@EqualsAndHashCode
public abstract class GeoJsonBase {
    @JsonProperty(Constants.TYPE)
    private String type;

    @JsonProperty(Constants.BBOX)
    private double[] bbox;

    private int dimension;
    boolean valid;

    GeoJsonBase(String type) {
        this.type = type;
        this.dimension = -1;
        this.valid = false;
    }
}
