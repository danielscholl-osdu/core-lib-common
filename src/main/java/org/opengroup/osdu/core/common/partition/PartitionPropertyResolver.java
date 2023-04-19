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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opengroup.osdu.core.common.cache.ICache;
import org.opengroup.osdu.core.common.model.http.AppException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "propertyResolver", name = "strategy", havingValue = "partition")
public class PartitionPropertyResolver implements IPropertyResolver {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final ICache<String, PartitionInfo> cache;
  private final IPartitionProvider partitionProvider;
  private final ISensitivePropertyResolver sensitivePropertyResolver;
  private static final String PARTITION_NOT_CONFIGURED = "Partition service not configured correctly for partition ";
  private static final String OPTIONAL_PROPERTY_NOT_RESOLVED = "Optional property not resolved.";

  @Override
  public String getPropertyValue(String propertyName, String partitionId) {
    try {
      PartitionInfo partitionInfo = getPartitionInfo(partitionId);
      Map<String, Property> partitionProperties = partitionInfo.getProperties();
      return getPropertyValue(partitionProperties, propertyName, partitionId);
    } catch (PartitionException e) {
      throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to get partition info",
          "Error getting partition info for partition: " + partitionId, e);
    }
  }

  @Override
  public Optional<String> getOptionalPropertyValue(String propertyName, String partitionId) {
    try {
      return Optional.of(getPropertyValue(propertyName, partitionId));
    } catch (AppException e) {
      log.warn(OPTIONAL_PROPERTY_NOT_RESOLVED, e);
    }
    return Optional.empty();
  }

  private PartitionInfo getPartitionInfo(String partitionId) throws PartitionException {
    PartitionInfo cachedPartitionInfo = cache.get(partitionId);
    if (cachedPartitionInfo == null) {
      PartitionInfo partitionInfo = partitionProvider.get(partitionId);
      cache.put(partitionId, partitionInfo);
      return partitionInfo;
    } else {
      return cachedPartitionInfo;
    }
  }

  @Override
  public String getPropertyValue(Map<String, Property> partitionProperties, String propertyName, String partitionId) {
    Optional<Property> optionalProperty = Optional.ofNullable(partitionProperties.get(propertyName));
    log.debug("Resolving property value for partition {} and property {}", partitionId, propertyName);

    if (optionalProperty.isPresent()) {
      Property property = optionalProperty.get();
      validateProperty(propertyName, partitionId, property);
      return resolveValue(propertyName, partitionId, property);
    } else {
      throw new AppException(HttpStatus.NOT_FOUND.value(),
          "Vital property not present in the Partition service",
          PARTITION_NOT_CONFIGURED + partitionId + ", missing property: "
              + propertyName);
    }
  }

  private void validateProperty(String propertyName, String partitionId, Property property) {
    if (Objects.isNull(property.getValue())) {
      throw new AppException(HttpStatus.NOT_FOUND.value(),
          "Property value not present in the Partition service",
          PARTITION_NOT_CONFIGURED + partitionId + ", missing property value : "
              + propertyName);
    }
  }

  private String resolveValue(String propertyName, String partitionId, Property property) {
    String partitionPropertyValue = property.getValue().toString();
    if (property.isSensitive()) {
      return sensitivePropertyResolver.getPropertyValue(partitionPropertyValue, partitionId);
    } else {
      log.debug("{} is not sensitive, the value: {} , provided by Partition will be used", propertyName, partitionPropertyValue);
      return partitionPropertyValue;
    }
  }

  @Override
  public Optional<String> getOptionalPropertyValue(Map<String, Property> partitionProperties, String propertyName,
      String partitionId) {
    try {
      return Optional.of(getPropertyValue(partitionProperties, propertyName, partitionId));
    } catch (AppException e) {
      log.warn(OPTIONAL_PROPERTY_NOT_RESOLVED, e);
    }
    return Optional.empty();
  }

  @Override
  public <T> T getPropertyValue(Map<String, Property> partitionProperties, String propertyName, String partitionId,
      Class<T> tClass) {
    String propertyValue = getPropertyValue(partitionProperties, propertyName, partitionId);
    try {
      return objectMapper.readValue(propertyValue, tClass);
    } catch (JsonProcessingException e) {
      log.warn(
          "Property {} present in partition: {}, but misconfigured, please review tenant configuration. Required format: {}",
          propertyName, partitionId, tClass, e);
      throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
          "Vital property misconfigured in the Partition service",
          PARTITION_NOT_CONFIGURED + partitionId + ", invalid property: "
              + propertyName, e);
    }
  }

  @Override
  public <T> Optional<T> getOptionalPropertyValue(Map<String, Property> partitionProperties, String propertyName,
      String partitionId, Class<T> tClass) {
    try {
      String propertyValue = getPropertyValue(partitionProperties, propertyName, partitionId);
      return Optional.of(objectMapper.readValue(propertyValue, tClass));
    } catch (AppException e) {
      log.warn(OPTIONAL_PROPERTY_NOT_RESOLVED, e);
    } catch (JsonProcessingException e) {
      log.warn("Optional property misconfigured, please review tenant configuration. Required format: {}", tClass, e);
    }
    return Optional.empty();
  }
}
