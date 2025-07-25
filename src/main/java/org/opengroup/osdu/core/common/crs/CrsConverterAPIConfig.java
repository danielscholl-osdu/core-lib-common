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

package org.opengroup.osdu.core.common.crs;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CrsConverterAPIConfig {
    @Builder.Default
    String rootUrl = "https://os-crs-converter-gae-dot-opendes.appspot.com/api/crs/v1";

    String apiKey;

    @Builder.Default
    private int connectTimeout = 5000;

    @Builder.Default
    private int connectionRequestTimeout = 60000;

    @Builder.Default
    private int socketTimeout = 60000;

    public static CrsConverterAPIConfig Default() {
        return CrsConverterAPIConfig.builder().build();
    }
}