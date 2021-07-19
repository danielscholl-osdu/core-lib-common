package org.opengroup.osdu.core.common.model.crs;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import lombok.Data;
import java.io.IOException;
import java.util.ArrayList;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
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

    public GeoJsonVariant getGeoJsonVariant(){
        if (this.getType().startsWith(ANY_CRS_PREFIX)) return GeoJsonVariant.ANY_CRS_GEO_JSON;
        return GeoJsonVariant.GEO_JSON;
    }

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

    public void setGeoJsonVariant(GeoJsonVariant gj_variant){
        ArrayList<GeoJsonBase> components = new ArrayList<>();
        this.appendParts(components);
        for (GeoJsonBase gb : components){
            gb.setGeoJsonVariantInternal(gj_variant);
        }
    }

    abstract void appendParts(ArrayList<GeoJsonBase> components);

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
}
