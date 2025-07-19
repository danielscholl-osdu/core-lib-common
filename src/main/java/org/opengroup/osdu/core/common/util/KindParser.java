// Copyright 2017-2022, Schlumberger
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

package org.opengroup.osdu.core.common.util;

import org.opengroup.osdu.core.common.SwaggerDoc;

import java.util.Arrays;
import java.util.List;

public class KindParser {
    public static List<String> parse(Object kind) {
        if (kind == null) {
            throw new IllegalArgumentException(SwaggerDoc.KIND_VALIDATION_CAN_NOT_BE_NULL_OR_EMPTY);
        }

        List<String> kinds;
        if (kind instanceof String) {
            // To remove extra spaces before and/or after the delimiter
            kinds = Arrays.asList(((String)kind).trim().split("\\s*,\\s*"));
        } else if (kind instanceof List<?>) {
            kinds = (List<String>) kind;
            // The above case is subtle. It won't throw exception even if kind is a list of integers or mixed types.
            // Check the type of each item
            for (int i = 0; i < kinds.size(); i++) {
                if (!(kinds.get(i) instanceof String)) {
                    throw new IllegalArgumentException(SwaggerDoc.KIND_VALIDATION_NOT_SUPPORTED_TYPE);
                }
            }
        } else {
            throw new IllegalArgumentException(SwaggerDoc.KIND_VALIDATION_NOT_SUPPORTED_TYPE);
        }

        return kinds;
    }
}
