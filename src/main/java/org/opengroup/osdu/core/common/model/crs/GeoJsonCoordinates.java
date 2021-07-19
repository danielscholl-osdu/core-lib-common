package org.opengroup.osdu.core.common.model.crs;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class GeoJsonCoordinates {
    private double[] xys;
    private double[] z_s;
    private int index;

    @Setter(AccessLevel.NONE)
    private int length;

    GeoJsonCoordinates(int length) {
        this.xys = new double[2 * length];
        this.z_s = new double[length];
        this.index = 0;
        this.length = length;
    }
}
