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

import java.util.Map;
import java.util.Optional;
import org.opengroup.osdu.core.common.model.http.AppException;

public interface IPropertyResolver {

  /**
   * @param propertyName desired property name, a map should contain it as a key
   * @param partitionId  partition id ex: "osdu"
   * @return property value as String, the resolution should be based on Property.isSensitive() param
   * @throws AppException if property value cannot be resolved
   */
  String getPropertyValue(String propertyName, String partitionId) throws AppException;

  /**
   * Unlike getPropertyValue() method, shouldn't throw exception if property is not possible to resolve.
   *
   * @param propertyName desired property name, a map should contain it as a key
   * @param partitionId  partition id ex: "osdu"
   * @return property value as type provided in parameters, the resolution should be based on Property.isSensitive()
   * param
   */
  Optional<String> getOptionalPropertyValue(String propertyName, String partitionId);

  /**
   * @param partitionProperties map with properties provided by Partition service
   * @param propertyName        desired property name, a map should contain it as a key
   * @param partitionId         partition id ex: "osdu"
   * @return property value as String, the resolution should be based on Property.isSensitive() param
   * @throws AppException if property value cannot be resolved
   */
  String getPropertyValue(Map<String, Property> partitionProperties, String propertyName, String partitionId)
      throws AppException;

  /**
   * Unlike getPropertyValue() method, shouldn't throw exception if property is not possible to resolve.
   *
   * @param partitionProperties map with properties provided by Partition service
   * @param propertyName        desired property name, a map should contain it as a key
   * @param partitionId         partition id ex: "osdu"
   * @return property value as String, the resolution should be based on Property.isSensitive() param
   */
  Optional<String> getOptionalPropertyValue(Map<String, Property> partitionProperties, String propertyName,
      String partitionId);

  /**
   * @param partitionProperties map with properties provided by Partition service
   * @param propertyName        desired property name, a map should contain it as a key
   * @param partitionId         partition id ex: "osdu"
   * @return property value as type provided in parameters, the resolution should be based on Property.isSensitive()
   * param
   * @throws AppException if property value cannot be resolved
   */
  <T> T getPropertyValue(Map<String, Property> partitionProperties, String propertyName, String partitionId,
      Class<T> tClass) throws AppException;

  /**
   * Unlike getPropertyValue() method, shouldn't throw exception if property is not possible to resolve.
   *
   * @param partitionProperties map with properties provided by Partition service
   * @param propertyName        desired property name, a map should contain it as a key
   * @param partitionId         partition id ex: "osdu"
   * @return property value as type provided in parameters, the resolution should be based on Property.isSensitive()
   * param
   */
  <T> Optional<T> getOptionalPropertyValue(Map<String, Property> partitionProperties, String propertyName,
      String partitionId, Class<T> tClass);
}
