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

package org.opengroup.osdu.core.common.partition;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClientBuilder;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;

public class PartitionFactory implements IPartitionFactory {

    private final PartitionAPIConfig config;
    CloseableHttpClient cacheHttpClient;

    public PartitionFactory(PartitionAPIConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("PartitionAPIConfig cannot be empty");
        }
        this.config = config;
        CacheConfig cacheConfig = CacheConfig.custom()
                .setMaxObjectSize(config.getMaxObjectSize())
                .setMaxCacheEntries(config.getMaxCacheEntries())
                .setSharedCache(false)
                .build();

        RequestConfig REQUEST_CONFIG = RequestConfig.custom()
                .setConnectTimeout(config.getConnectTimeout())
                .setConnectionRequestTimeout(config.getConnectionRquestTimeout())
                .setSocketTimeout(config.getSocketTimeout()).build();

        this.cacheHttpClient = CachingHttpClientBuilder
                .create()
                .setCacheConfig(cacheConfig)
                .setDefaultRequestConfig(REQUEST_CONFIG)
                .build();
    }

    @Override
    public IPartitionProvider create(DpsHeaders headers) {
        if (headers == null) {
            throw new NullPointerException("headers cannot be null");
        }
        return new PartitionService(this.config, headers, cacheHttpClient);
    }
}