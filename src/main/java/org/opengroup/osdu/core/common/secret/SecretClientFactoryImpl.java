/*
 Copyright 2020-2024 Google LLC
 Copyright 2020-2024 EPAM Systems, Inc

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

package org.opengroup.osdu.core.common.secret;

import org.opengroup.osdu.core.common.http.HttpClient;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.util.IServiceAccountJwtClient;

public class SecretClientFactoryImpl implements SecretClientFactory {
  private final SecretAPIConfig config;

  public SecretClientFactoryImpl(SecretAPIConfig config) {
    if (config == null) {
      throw new IllegalArgumentException("SecretAPIConfig cannot be empty");
    }
    this.config = config;
  }

  @Override
  public SecretClient create(
      DpsHeaders headers,
      IServiceAccountJwtClient serviceAccountJwtClient,
      AccessGroups accessGroups) {
    if (headers == null) {
      throw new NullPointerException("headers cannot be null");
    }
    return new SecretClientImpl(
        this.config, new HttpClient(), headers, serviceAccountJwtClient, accessGroups);
  }
}
