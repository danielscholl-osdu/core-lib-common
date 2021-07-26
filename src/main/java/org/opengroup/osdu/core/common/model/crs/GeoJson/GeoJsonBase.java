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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.IOException;
import java.util.ArrayList;

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

    private static final String ANY_CRS_PREFIX = "AnyCrs";

    abstract Object getCoordinatesArray();

    public enum GeoJsonVariant { GEO_JSON, ANY_CRS_GEO_JSON }

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

    public static GeoJsonBase createInstance(String json) {
        GeoJsonBase result;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode node = mapper.readTree(json);
            result = mapper.treeToValue(node, GeoJsonBase.class);
            result.setValid(result.isValid());
        } catch (IOException e) {
            return null;
        }
        return result;
    }

    public String toJsonString() {
        String result;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            result = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return null;
        }
        return result;
    }

    abstract public void updateBbox();

    abstract public boolean isValid();

    abstract int getLength();

    private void setGeoJsonVariantInternal(GeoJsonVariant gj_variant){
        if (gj_variant == GeoJsonVariant.ANY_CRS_GEO_JSON) {
            if (!this.getType().startsWith(ANY_CRS_PREFIX)) {
                this.setType(ANY_CRS_PREFIX+this.getType());
            }
        } else {
            if (this.getType().startsWith(ANY_CRS_PREFIX)) {
                this.setType(this.getType().replace(ANY_CRS_PREFIX, ""));
            }
        }
    }

    abstract void appendParts(ArrayList<GeoJsonBase> components);

    static double[] getMinMax(double[] coordinates, int dimension) {
        double[] bbox = new double[dimension * 2];
        for (int i = 0; i < dimension; i++) {
            bbox[i] = coordinates[i];
            bbox[i + dimension] = bbox[i];
        }
        return bbox;
    }

    static double[] getMinMax(double[][] coordinates, int dimension){
        double[] bbox = new double[dimension * 2];
        for (int i = 0; i < dimension; i++) {
            bbox[i] = coordinates[0][i];
            bbox[i + dimension] = coordinates[0][i];
        }
        for (int j = 1; j < coordinates.length; j++) {
            for (int i = 0; i < dimension; i++) {
                bbox[i] = Double.min(bbox[i], coordinates[j][i]);
                bbox[i + dimension] = Double.max(bbox[i + dimension], coordinates[j][i]);
            }
        }
        return bbox;
    }

    static double[] getMinMax(double[][][] coordinates, int dimension){
        double[] bbox = new double[dimension * 2];
        for (int i = 0; i < dimension; i++) {
            bbox[i] = coordinates[0][0][i];
            bbox[i + dimension] = coordinates[0][0][i];
        }
        for (double[][] coordinate : coordinates) {
            for (int j = 1; j < coordinate.length; j++) {
                for (int i = 0; i < dimension; i++) {
                    bbox[i] = Double.min(bbox[i], coordinate[j][i]);
                    bbox[i + dimension] = Double.max(bbox[i + dimension], coordinate[j][i]);
                }
            }
        }
        return bbox;
    }

    static double[] getMinMax(double[][][][] coordinates, int dimension) {
        double[] bbox = new double[dimension * 2];
        for (int i = 0; i < dimension; i++) {
            bbox[i] = coordinates[0][0][0][i];
            bbox[i + dimension] = coordinates[0][0][0][i];
        }
        for (double[][][] coordinate : coordinates) {
            for (int k = 1; k < coordinate.length; k++) {
                for (int j = 1; j < coordinate[k].length; j++) {
                    for (int i = 0; i < dimension; i++) {
                        bbox[i] = Double.min(bbox[i], coordinate[k][j][i]);
                        bbox[i + dimension] = Double.max(bbox[i + dimension], coordinate[k][j][i]);
                    }
                }
            }
        }
        return bbox;
    }
}
