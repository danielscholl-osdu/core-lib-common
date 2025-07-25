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

package org.opengroup.osdu.core.common.model.indexer;

public enum StorageType {

    LINK("link"),

    LINK_ARRAY("[]link"),

    BOOLEAN("boolean"),

    BOOLEAN_ARRAY("[]boolean"),

    STRING("string"),

    STRING_ARRAY("[]string"),

    INT("int"),

    INT_ARRAY("[]int"),

    FLOAT("float"),

    FLOAT_ARRAY("[]float"),

    DOUBLE("double"),

    DOUBLE_ARRAY("[]double"),

    LONG("long"),

    LONG_ARRAY("[]long"),

    DATETIME("datetime"),

    DATETIME_ARRAY("[]datetime"),

    GEO_POINT("core:dl:geopoint:1.0.0"),

    GEO_SHAPE("core:dl:geoshape:1.0.0");

    private final String value;

    StorageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}