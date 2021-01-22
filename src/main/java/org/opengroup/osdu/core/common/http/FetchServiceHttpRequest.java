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

package org.opengroup.osdu.core.common.http;

import org.opengroup.osdu.core.common.model.http.DpsHeaders;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FetchServiceHttpRequest {
    private String httpMethod;
    private String url;
    private String body;
    private Map<String, String> queryParams;
    private DpsHeaders headers;

    public static class FetchServiceHttpRequestBuilder {

        private DpsHeaders headers;

        public FetchServiceHttpRequestBuilder headers(Map<String, String> headers) {
            this.headers = DpsHeaders.createFromMap(headers);
            return this;
        }

        public FetchServiceHttpRequestBuilder headers(DpsHeaders headers) {
            this.headers = headers;
            return this;
        }
    }
}
