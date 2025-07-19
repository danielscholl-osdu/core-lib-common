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

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Component
public class CrsPropertySet {

	private Set<String> nestedPropertyNames;
	private Map<String, String> propertyPairing;

	public Set<String> getNestedPropertyNames() {
		if (this.nestedPropertyNames == null) {
			this.nestedPropertyNames = new HashSet<>();
		}

		this.nestedPropertyNames.add("projectOutlineLocalGeographic");
		this.nestedPropertyNames.add("projectOutlineProjected");

		return this.nestedPropertyNames;
	}

	public Map<String, String> getPropertyPairing() {
		if (this.propertyPairing == null) {
			this.propertyPairing = new HashMap<>();
		}

		this.propertyPairing.put("x", "y");
		this.propertyPairing.put("lon", "lat");
		this.propertyPairing.put("long", "lat");
		this.propertyPairing.put("longitude", "latitude");
		this.propertyPairing.put("wlbewutm", "wlbnsutm");
		this.propertyPairing.put("wlbewdesdeg", "wlbnsdecdeg");
		this.propertyPairing.put("topholexng", "topholeyng");
		this.propertyPairing.put("topholexdd", "topholeydd");
		this.propertyPairing.put("bhlongitude", "bhlatitude");
		this.propertyPairing.put("utm_x", "utm_y");

		return this.propertyPairing;
	}
}
