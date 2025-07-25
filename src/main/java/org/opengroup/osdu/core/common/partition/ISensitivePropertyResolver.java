/*
 *  Copyright 2020-2023 Google LLC
 *  Copyright 2020-2023 EPAM Systems, Inc
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

package org.opengroup.osdu.core.common.partition;

import org.opengroup.osdu.core.common.model.http.AppException;

public interface ISensitivePropertyResolver {

  /**
   * @param referenceName desired property name in sensitive properties storage
   * @param partitionId  partition id ex: "osdu"
   * @return property value as String
   * @throws AppException if property value cannot be resolved
   */
  String getPropertyValue(String referenceName, String partitionId) throws AppException;
}
