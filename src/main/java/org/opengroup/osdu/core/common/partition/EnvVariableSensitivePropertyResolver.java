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

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.opengroup.osdu.core.common.model.http.AppException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnMissingBean(type = "ISensitivePropertyResolver")
public class EnvVariableSensitivePropertyResolver implements ISensitivePropertyResolver{

  @Override
  public String getPropertyValue(String propertyValue, String partitionId) {
    try {
      return getEnvVariableByName(propertyValue, partitionId);
    } catch (SecurityException e) {
      log.error("Security exception, unable to read env variable.", e);
      throw new AppException(HttpStatus.NOT_FOUND.value(),
          "Security exception, unable to read env variable ", "Environment not configured correctly");
    }
  }

  private String getEnvVariableByName(String envVariableName, String partitionId) {
    log.debug("Property is sensitive, the value will be resolved with the environment variable {}", envVariableName);
    String envValue = System.getenv(envVariableName);
    if (Objects.isNull(envValue)) {
      throw new AppException(HttpStatus.NOT_FOUND.value(),
          "Vital property not present in the environment",
          envVariableName + " not configured correctly for partition " + partitionId);
    }
    return envValue;
  }
}
