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

package org.opengroup.osdu.core.common.util;

import com.google.gson.Gson;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.opengroup.osdu.core.common.model.http.AppException;
import org.opengroup.osdu.core.common.model.tenant.TenantInfo;
import org.opengroup.osdu.core.common.partition.PartitionInfo;
import org.opengroup.osdu.core.common.partition.Property;
import org.springframework.http.HttpStatus;

public class PartitionInfoToTenantInfoMapper {

    private static final String TENANT_NAME = "name";
    private static final String DATA_PARTITION = "dataPartitionId";
    private static final String TENANT_PROJECT = "projectId";
    private static final String COMPLIANCE_RULE_SET = "complianceRuleSet";
    private static final String TENANT_SERVICE_ACCOUNT = "serviceAccount";
    private static final String TENANT_CRM_ACCOUNT_IDS = "crmAccountID";

    private PartitionInfoToTenantInfoMapper() {
    }

    public static TenantInfo mapToTenantInfo(PartitionInfo partitionInfo) {
        if(Objects.isNull(partitionInfo)){
            return null;
        }
        TenantInfo tenantInfo = new TenantInfo();
        Map<String, Property> properties = partitionInfo.getProperties();

        tenantInfo.setName(getValue(TENANT_NAME, properties, String.class));
        tenantInfo.setDataPartitionId(getValue(DATA_PARTITION, properties, String.class));
        tenantInfo.setProjectId(getValue(TENANT_PROJECT, properties, String.class));
        tenantInfo.setComplianceRuleSet(getValue(COMPLIANCE_RULE_SET, properties, String.class));
        tenantInfo.setServiceAccount((getValue(TENANT_SERVICE_ACCOUNT, properties, String.class)));
        List crmAccountIds = new Gson().fromJson(getValue(TENANT_CRM_ACCOUNT_IDS, properties, String.class), List.class);
        tenantInfo.setCrmAccountIds(crmAccountIds);
        return tenantInfo;
    }

    private static <T> T getValue(String propertyName, Map<String, Property> properties, Class<T> vClass) {
        Property property = properties.get(propertyName);
        if (Objects.isNull(property)) {
            throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "TenantInfo misconfiguration",
                String.format("%s property not present in tenant info", propertyName));
        }
        T value = (T) property.getValue();
        if (Objects.isNull(value)) {
            throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "TenantInfo misconfiguration",
                String.format("%s property misconfigured in tenant info", propertyName));
        }
        return value;
    }

}
