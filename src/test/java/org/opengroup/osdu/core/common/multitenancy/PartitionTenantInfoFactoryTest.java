/*
 * Copyright 2020 Google LLC
 * Copyright 2020 EPAM Systems, Inc
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.opengroup.osdu.core.common.cache.ICache;
import org.opengroup.osdu.core.common.http.HttpResponse;
import org.opengroup.osdu.core.common.logging.ILogger;
import org.opengroup.osdu.core.common.model.http.AppException;
import org.opengroup.osdu.core.common.model.tenant.TenantInfo;
import org.opengroup.osdu.core.common.partition.IPartitionProvider;
import org.opengroup.osdu.core.common.partition.PartitionException;
import org.opengroup.osdu.core.common.partition.PartitionInfo;
import org.opengroup.osdu.core.common.util.PartitionAndTenantInfoStub;

@RunWith(MockitoJUnitRunner.class)
public class PartitionTenantInfoFactoryTest {

    private PartitionAndTenantInfoStub infoStub = new PartitionAndTenantInfoStub();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    private ILogger logger;

    @Mock
    private ICache<String, TenantInfo> tenantCache;

    @Mock
    private IPartitionProvider partitionProvider;

    private PartitionTenantInfoFactory tenantInfoFactory;

    private TenantInfo tenantInfo;

    private PartitionInfo partitionInfo;

    private String tenantName;

    @Before
    public void setUp() {
        tenantInfoFactory = PartitionTenantInfoFactory.builder()
            .logger(logger)
            .tenantCache(tenantCache)
            .partitionProvider(partitionProvider)
            .build();

        tenantInfo = infoStub.getTenantInfo();
        partitionInfo = infoStub.getPartitionInfo();
        tenantName = infoStub.getTenantName();
    }

    @Test
    public void testExistInCache() {
        Mockito.when(tenantCache.get(tenantName)).thenReturn(tenantInfo);
        assertTrue(tenantInfoFactory.exists(tenantName));
    }

    @Test
    public void testExistInPartitionService() throws PartitionException {
        Mockito.when(partitionProvider.get(tenantName)).thenReturn(partitionInfo);
        assertTrue(tenantInfoFactory.exists(tenantName));
    }

    @Test
    public void testGetTenantInfoWithCache() {
        Mockito.when(tenantCache.get(tenantName)).thenReturn(tenantInfo);
        assertEquals(tenantInfo, tenantInfoFactory.getTenantInfo(tenantName));
    }

    @Test
    public void testGetTenantInfoWithPartitionService() throws PartitionException {
        Mockito.when(partitionProvider.get(tenantName)).thenReturn(partitionInfo);
        assertEquals(tenantInfo, tenantInfoFactory.getTenantInfo(tenantName));
    }

    @Test
    public void testListTenantInfo() throws PartitionException {
        Mockito.when(partitionProvider.list()).thenReturn(Collections.singletonList(tenantName));
        Mockito.when(tenantCache.get(tenantName)).thenReturn(tenantInfo);
        assertTrue(tenantInfoFactory.listTenantInfo().contains(tenantInfo));
    }

    @Test
    public void testExpectedExceptionWhenListTenantInfo() throws PartitionException {

        Mockito.when(partitionProvider.list()).thenReturn(Collections.singletonList(tenantName));
        Mockito.when(partitionProvider.get(tenantName)).thenThrow(new AppException(404, "Partition does not exist.", "Partition not found."));
        Collection<TenantInfo> tenantInfos = tenantInfoFactory.listTenantInfo();
        Mockito.verify(logger).warning("core-common-partition", "Partition not found.", Collections.emptyMap());
        assertTrue(tenantInfos.isEmpty());
    }

    @Test
    public void testExpectedExceptionWhenGetTenantInfo() throws PartitionException {
        HttpResponse response = new HttpResponse();
        response.setBody("Partition does not exist.");
        response.setResponseCode(404);
        exception.expect(AppException.class);
        exception.expectMessage("Partition does not exist.");
        Mockito.when(partitionProvider.get(tenantName)).thenThrow(new PartitionException("Partition does not exist.", response));
        tenantInfoFactory.getTenantInfo(tenantName);
    }

}