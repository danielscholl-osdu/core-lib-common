/*
 * Copyright 2021 Google LLC
 * Copyright 2021 EPAM Systems, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opengroup.osdu.core.common.multitenancy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.Builder;
import org.opengroup.osdu.core.common.cache.ICache;
import org.opengroup.osdu.core.common.cache.RedisCache;
import org.opengroup.osdu.core.common.cache.TenantSafeCache;
import org.opengroup.osdu.core.common.cache.VmCache;
import org.opengroup.osdu.core.common.logging.DefaultLogger;
import org.opengroup.osdu.core.common.logging.ILogger;
import org.opengroup.osdu.core.common.model.http.AppException;
import org.opengroup.osdu.core.common.model.tenant.TenantInfo;
import org.opengroup.osdu.core.common.partition.IPartitionProvider;
import org.opengroup.osdu.core.common.partition.PartitionException;
import org.opengroup.osdu.core.common.partition.PartitionInfo;
import org.opengroup.osdu.core.common.provider.interfaces.ITenantFactory;
import org.opengroup.osdu.core.common.util.PartitionInfoToTenantInfoMapper;

public class PartitionTenantInfoFactory implements ITenantFactory {

    private ICache<String, TenantInfo> tenantCache;

    private IPartitionProvider partitionProvider;

    private ILogger logger;

    private String logPrefix;

    @Builder
    public PartitionTenantInfoFactory(
        ICache<String, TenantInfo> tenantCache, IPartitionProvider partitionProvider, ILogger logger, String logPrefix) {
        this.tenantCache = Optional.ofNullable(tenantCache).orElse(new VmCache<>(7200, 100));
        this.partitionProvider = partitionProvider;
        this.logger = Optional.ofNullable(logger).orElse(new DefaultLogger());
        this.logPrefix = Optional.ofNullable(logPrefix).orElse("core-common-partition");
    }

    @Override
    public boolean exists(String tenantName) {
        TenantInfo tenantInfo;
        try {
            tenantInfo = getTenantInfoFromCache(tenantName);
            return Objects.nonNull(tenantInfo);
        } catch (PartitionException e) {
            logger.warning(logPrefix, e.getMessage(), Collections.emptyMap());
            throw new AppException(e.getHttpResponse().getResponseCode(), "Partition get partition error", e.getResponse().getBody(), e);
        }
    }


    @Override
    public TenantInfo getTenantInfo(String tenantName) {
        try {
            return getTenantInfoFromCache(tenantName);
        } catch (PartitionException e) {
            logger.warning(logPrefix, e.getMessage(), Collections.emptyMap());
            throw new AppException(e.getHttpResponse().getResponseCode(), "Partition get partition error", e.getResponse().getBody(), e);
        }
    }

    @Override
    public Collection<TenantInfo> listTenantInfo() {
        try {
            ArrayList<TenantInfo> tenantInfos = new ArrayList<>();
            List<String> partitionsList = partitionProvider.list();
            for (String partition : partitionsList) {
                try {
                    tenantInfos.add(getTenantInfoFromCache(partition));
                } catch (AppException e) {
                    logger.warning(logPrefix, e.getMessage(), Collections.emptyMap());
                    continue;
                }
            }
            return tenantInfos;
        } catch (PartitionException e) {
            logger.warning(logPrefix, e.getMessage(), Collections.emptyMap());
            throw new AppException(e.getHttpResponse().getResponseCode(), "Partition-service list partitions error", e.getResponse().getBody(), e);
        }
    }

    @Override
    public <V> ICache<String, V> createCache(String tenantName, String host, int port, int expireTimeSeconds, Class<V> classOfV) {
        TenantInfo info = this.getTenantInfo(tenantName);
        if (info == null) {
            return null;
        }
        return new TenantSafeCache<>(tenantName, new RedisCache<>(host, port, expireTimeSeconds, String.class, classOfV));
    }

    @Override
    public void flushCache() {
        tenantCache.clearAll();
    }

    private TenantInfo getTenantInfoFromCache(String tenantName) throws PartitionException {
        TenantInfo tenantInfo;
        tenantInfo = this.tenantCache.get(tenantName);
        if (Objects.isNull(tenantInfo)) {
            PartitionInfo partitionInfo = partitionProvider.get(tenantName);
            tenantInfo = PartitionInfoToTenantInfoMapper.mapToTenantInfo(partitionInfo);
            tenantCache.put(tenantName, tenantInfo);
        }
        return tenantInfo;
    }
}
