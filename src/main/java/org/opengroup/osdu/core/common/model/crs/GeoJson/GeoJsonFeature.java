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
