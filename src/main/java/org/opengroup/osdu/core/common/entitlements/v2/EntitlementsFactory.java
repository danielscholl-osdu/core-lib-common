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

package org.opengroup.osdu.core.common.entitlements.v2;

import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.http.json.HttpResponseBodyMapper;
import org.opengroup.osdu.core.common.http.HttpClient;

public class EntitlementsFactory implements IEntitlementsFactory {

    private final EntitlementsAPIConfig config;
    private final HttpResponseBodyMapper mapper;

    public EntitlementsFactory(EntitlementsAPIConfig config, HttpResponseBodyMapper mapper) {
        if (config == null) {
            throw new IllegalArgumentException("EntitlementsAPIConfig cannot be empty");
        }
        this.config = config;
        this.mapper = mapper;
    }

    @Override
    public IEntitlementsService create(DpsHeaders headers) {
        if (headers == null) {
            throw new NullPointerException("headers cannot be null");
        }
        return new EntitlementsService(this.config, new HttpClient(), headers, mapper);
    }
}