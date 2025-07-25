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

import com.google.common.base.Strings;

public enum ElasticType {

    KEYWORD("keyword"),

    CONSTANT_KEYWORD("constant_keyword"),

    TEXT("text"),

    DATE("date"),

    NESTED("nested"),

    OBJECT("object"),

    FLATTENED("flattened"),

    GEO_POINT("geo_point"),

    GEO_SHAPE("geo_shape"),

    INTEGER("integer"),

    LONG("long"),

    FLOAT("float"),

    DOUBLE("double"),

    BOOLEAN("boolean"),

    // arrays only used for validations, elastic supports this out of the box
    KEYWORD_ARRAY("keyword_array"),

    TEXT_ARRAY("text_array"),

    INTEGER_ARRAY("integer_array"),

    LONG_ARRAY("long_array"),

    FLOAT_ARRAY("float_array"),

    DOUBLE_ARRAY("double_array"),

    BOOLEAN_ARRAY("boolean_array"),

    DATE_ARRAY("date_array"),

    UNDEFINED("undefined");

    private final String value;

    ElasticType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ElasticType forValue(String value) {

        if (Strings.isNullOrEmpty(value)) return ElasticType.UNDEFINED;

        for (ElasticType type : values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return ElasticType.UNDEFINED;
    }
}
