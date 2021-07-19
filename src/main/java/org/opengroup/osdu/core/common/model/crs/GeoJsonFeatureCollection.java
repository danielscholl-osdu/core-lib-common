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

package org.opengroup.osdu.core.common.model.crs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel
public class GeoJsonFeatureCollection extends GeoJsonBase {
    @JsonProperty("features")
    private GeoJsonFeature[] features;

    @JsonProperty("properties")
    private Object properties;

    @JsonProperty("persistableReferenceCrs")
    private String persistableReferenceCrs;

    @JsonProperty("persistableReferenceUnitZ")
    private String persistableReferenceUnitZ;

    public GeoJsonFeatureCollection() {
        super("AnyCrsFeatureCollection"); // default to non-GeoJSON
    }

    @Override
    Object getCoordinatesArray() {
        return null;
    }

    @Override
    public void updateBbox() {
        if (this.getBbox() == null) return;  // if there was no bounding box before, don't bother
        int dimension = this.getDimension();
        double[][] coordinates = new double[this.getFeatures().length * 2][dimension];
        for (int i = 0; i < this.getFeatures().length; i++) {
            GeoJsonBase gb = this.getFeatures()[i];
            gb.updateBbox();
            System.arraycopy(gb.getBbox(), 0, coordinates[2*i], 0, gb.getBbox().length/2);
            System.arraycopy(gb.getBbox(), dimension, coordinates[2*i+1], 0, gb.getBbox().length/2);
        }
        this.setBbox(getMinMax(coordinates, dimension));
    }

    @Override
    public boolean isValid() {
        boolean ok = this.features != null;
        int d = 3;  // 3 dimensional point are default
        if (ok) {
            for (GeoJsonFeature g : this.getFeatures()) {
                ok = ok && g.isValid();
                d = Math.min(g.getDimension(), d);
            }
        }
        if (ok) {
            this.setDimension(d);
        } else {
            this.setDimension(-1);
        }
        return ok;
    }

    @Override
    public int getLength() {
        int length = 0;
        if (this.isValid()) {
            for (GeoJsonFeature g : this.getFeatures()) {
                length += g.getLength();
            }
        }
        return length;
    }

    @Override
    void appendParts(ArrayList<GeoJsonBase> components) {
        components.add(this);
        if (this.getFeatures() != null) {
            for (GeoJsonBase g : this.getFeatures()) {
                g.appendParts(components);
            }
        }
    }
}
