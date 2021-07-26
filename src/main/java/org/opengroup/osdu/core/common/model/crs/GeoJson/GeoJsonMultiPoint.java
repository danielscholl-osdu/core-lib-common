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

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@EqualsAndHashCode(callSuper = true)

public class GeoJsonMultiPoint extends GeoJsonBase {
    @JsonProperty("coordinates")
    private double[][] coordinates;

    public GeoJsonMultiPoint() {
        super("AnyCrsMultiPoint");
    } // default to non-GeoJSON

    @Override
    public void updateBbox() {
        this.setBbox(getMinMax(this.getCoordinates(), this.getDimension()));
    }

    @Override
    public boolean isValid() {
        boolean ok = this.coordinates != null && this.coordinates.length >= 1;
        if (ok) {
            int d = 3;
            for (double[] pt : this.getCoordinates()) {
                d = Math.min(pt.length, d);
                ok = ok && pt.length >= 2;
            }
            if (ok) {
                this.setDimension(d);
            } else {
                this.setDimension(-1);
            }
        }
        return ok;
    }

    @Override
    public int getLength() {
        return this.isValid() ? this.getCoordinates().length : 0;
    }

    @Override
    void appendParts(ArrayList<GeoJsonBase> components) {
        components.add(this);
    }

    @Override
    Object getCoordinatesArray(){
        return this.getCoordinates();
    }
}
