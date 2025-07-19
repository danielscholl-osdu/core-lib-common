/*
 *  Copyright 2020-2025 Google LLC
 *  Copyright 2020-2025 EPAM Systems, Inc
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.opengroup.osdu.core.common.feature;

import java.util.List;
import org.opengroup.osdu.core.common.model.info.FeatureFlagStateResolver;
import org.opengroup.osdu.core.common.model.info.FeatureFlagStateResolver.FeatureFlagState;
import org.opengroup.osdu.core.common.model.tenant.TenantInfo;
import org.opengroup.osdu.core.common.multitenancy.ITenantInfoService;

public class CommonFeatureFlagStateResolverUtil {

  public static FeatureFlagStateResolver buildCommonFFStateResolver(String featureFlagName,
      ITenantInfoService tenantInfoService, IFeatureFlag featureFlagService) {
    return () -> {
      List<TenantInfo> allTenantInfo = tenantInfoService.getAllTenantInfos();
      return allTenantInfo.stream()
          .map(tenantInfo -> FeatureFlagState.builder()
              .partition(tenantInfo.getName())
              .name(featureFlagName)
              .source(featureFlagService.source())
              .enabled(featureFlagService.isFeatureEnabled(featureFlagName, tenantInfo.getName()))
              .build())
          .toList();
    };
  }
}
