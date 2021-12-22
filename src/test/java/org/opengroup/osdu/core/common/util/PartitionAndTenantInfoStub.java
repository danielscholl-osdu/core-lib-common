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

package org.opengroup.osdu.core.common.util;

import java.util.Arrays;
import java.util.HashMap;
import org.opengroup.osdu.core.common.model.tenant.TenantInfo;
import org.opengroup.osdu.core.common.partition.PartitionInfo;
import org.opengroup.osdu.core.common.partition.Property;

public class PartitionAndTenantInfoStub {

    private final String TENANT_NAME = "name";
    private final String DATA_PARTITION = "dataPartitionId";
    private final String TENANT_PROJECT = "projectId";
    private final String COMPLIANCE_RULE_SET = "complianceRuleSet";
    private final String TENANT_SERVICE_ACCOUNT = "serviceAccount";
    private final String TENANT_CRM_ACCOUNT_IDS = "crmAccountID";

    private final String TENANT_VALUE = "tenant";
    private final String PARTITION_VALUE = "data-partition";
    private final String PROJECT_VALUE = "project";
    private final String SHARED_VALUE = "shared";
    private final String SERVICE_ACC_VALUE = "service@service.com";
    private final String CRM_ACCOUNT_VALUE = "[\"cicd\",\"opendes\"]";

    private PartitionInfo partitionInfo;

    private TenantInfo tenantInfo;

    public PartitionAndTenantInfoStub() {
        partitionInfo =
            PartitionInfo.builder()
                .properties(
                    new HashMap<String, Property>() {{
                        put(TENANT_NAME, Property.builder().sensitive(false).value(TENANT_VALUE).build());
                        put(DATA_PARTITION, Property.builder().sensitive(false).value(PARTITION_VALUE).build());
                        put(TENANT_PROJECT, Property.builder().sensitive(false).value(PROJECT_VALUE).build());
                        put(COMPLIANCE_RULE_SET, Property.builder().sensitive(false).value(SHARED_VALUE).build());
                        put(TENANT_SERVICE_ACCOUNT, Property.builder().sensitive(false).value(SERVICE_ACC_VALUE).build());
                        put(TENANT_CRM_ACCOUNT_IDS, Property.builder().sensitive(false).value(CRM_ACCOUNT_VALUE).build());
                    }})
                .build();

        tenantInfo = new TenantInfo();
        tenantInfo.setName(TENANT_VALUE);
        tenantInfo.setDataPartitionId(PARTITION_VALUE);
        tenantInfo.setProjectId(PROJECT_VALUE);
        tenantInfo.setComplianceRuleSet(SHARED_VALUE);
        tenantInfo.setServiceAccount(SERVICE_ACC_VALUE);
        tenantInfo.setCrmAccountIds(Arrays.asList("cicd", "opendes"));
    }

    public String getTenantName() {
        return TENANT_VALUE;
    }

    public PartitionInfo getPartitionInfo() {
        return partitionInfo;
    }

    public TenantInfo getTenantInfo() {
        return tenantInfo;
    }
}
