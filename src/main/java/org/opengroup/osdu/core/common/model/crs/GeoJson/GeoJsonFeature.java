package org.opengroup.osdu.core.common.model.crs.GeoJson;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;

@Data
@EqualsAndHashCode(callSuper = true)
public class GeoJsonFeature extends GeoJsonBase {
    @JsonProperty("geometry")
    private GeoJsonBase geometry;

    @JsonProperty("properties")
    private Object properties;

    public GeoJsonFeature() {
        super("AnyCrsFeature"); // default to non-GeoJSON
        this.properties = new Object();
    }

    @Override
    Object getCoordinatesArray() {
        return null;
    }

    @Override
    public void updateBbox() {
        int dimension = this.getDimension();
        GeoJsonBase gb = this.getGeometry();
        gb.updateBbox();
        this.setBbox(gb.getBbox());
    }

    @Override
    public boolean isValid() {
        boolean ok = this.geometry != null && this.geometry.isValid();
        if (ok) {
            this.setDimension(this.geometry.getDimension());
        } else {
            this.setDimension(-1);
        }
        return ok;
    }

    @Override
    int getLength() {
        int length = 0;
        if (this.isValid()) {
            length = this.getGeometry().getLength();
        }
        return length;
    }

    @Override
    void appendParts(ArrayList<GeoJsonBase> components) {
        components.add(this);
        if (this.getGeometry() != null) this.getGeometry().appendParts(components);
    }
}
