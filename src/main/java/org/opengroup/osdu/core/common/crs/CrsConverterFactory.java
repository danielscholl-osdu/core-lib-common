// Copyright 2017-2023, Schlumberger
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

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.opengroup.osdu.core.common.http.json.HttpResponseBodyMapper;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;

public class CrsConverterFactory implements ICrsConverterFactory {

    private final CrsConverterAPIConfig config;
    private final HttpResponseBodyMapper mapper;
    private CloseableHttpClient httpClient;

    public CrsConverterFactory(CrsConverterAPIConfig config, HttpResponseBodyMapper mapper) {
        if (config == null) {
            throw new IllegalArgumentException("CrsConverterAPIConfig cannot be empty");
        }
        this.config = config;
        this.mapper = mapper;

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(config.getConnectTimeout())
                .setConnectionRequestTimeout(config.getConnectionRequestTimeout())
                .setSocketTimeout(config.getSocketTimeout())
                .build();

        this.httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
    }

    @Override
    public ICrsConverterService create(DpsHeaders headers) {
        if (headers == null) {
            throw new NullPointerException("headers cannot be null");
        }
        return new CrsConverterService(this.config, httpClient, headers, mapper);
    }

    @Override
    public ICrsConverterService create(DpsHeaders headers, RequestConfig requestConfig) {
        if (headers == null) {
            throw new NullPointerException("headers cannot be null");
        } else {
            this.httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
            return new CrsConverterService(this.config, httpClient, headers, mapper);
        }
    }
}