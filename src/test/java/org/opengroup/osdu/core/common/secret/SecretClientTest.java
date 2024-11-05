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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.opengroup.osdu.core.common.http.HttpRequest;
import org.opengroup.osdu.core.common.http.HttpResponse;
import org.opengroup.osdu.core.common.http.IHttpClient;
import org.opengroup.osdu.core.common.model.http.AppException;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.util.IServiceAccountJwtClient;

@RunWith(MockitoJUnitRunner.class)
public class SecretClientTest {

  private static final String SECRET_KEY_1_VAL = "secret_key_1";
  private static final String SECRET_VALUE_1_VAL = "secret_value_1";
  private static final String SECRET_VALUE_2_VAL = "secret_value_2";
  private static final String TENANT_1_VAL = "tenant_1";
  private static final String TOKEN_1_VAL = "token_1";
  private static final String SECRET_API = "secret_host";

  @Mock private SecretAPIConfig configurationProperties;

  @Mock private DpsHeaders dpsHeaders;

  @Mock private IServiceAccountJwtClient tokenService;

  @Mock private IHttpClient httpClient;

  @Mock private AccessGroups accessGroups;

  @InjectMocks private SecretClientImpl secretClient;

  @Before
  public void setUp() {
    when(dpsHeaders.getPartitionId()).thenReturn(TENANT_1_VAL);
    when(tokenService.getIdToken(any())).thenReturn(TOKEN_1_VAL);
    when(accessGroups.accessOwnersGroup()).thenReturn("");
    when(accessGroups.accessOwnersGroup()).thenReturn("");
    when(configurationProperties.getSecretApi()).thenReturn(SECRET_API);
  }

  @Test
  public void createSecretTest() {
    SecretModel expectedResult =
        SecretModel.builder()
            .id(SECRET_KEY_1_VAL)
            .value(SECRET_VALUE_1_VAL)
            .isEnabled(true)
            .build();
    HttpResponse httpResponse = createHttpResponse(expectedResult, 200);

    when(httpClient.send(any(HttpRequest.class))).thenReturn(httpResponse);

    SecretModel actualResult = secretClient.createSecret(SECRET_KEY_1_VAL, SECRET_VALUE_1_VAL);

    assertEquals(expectedResult, actualResult);
  }

  @Test
  public void exceptionCreateSecretTest() {
    HttpResponse httpResponse = new HttpResponse();
    httpResponse.setBody("invalid_body");
    httpResponse.setResponseCode(404);

    when(httpClient.send(any(HttpRequest.class))).thenReturn(httpResponse);

    assertThrows(
        AppException.class, () -> secretClient.createSecret(SECRET_KEY_1_VAL, SECRET_VALUE_1_VAL));
  }

  @Test
  public void retrieveSecretTest() {
    SecretModel expectedResult =
        SecretModel.builder()
            .id(SECRET_KEY_1_VAL)
            .value(SECRET_VALUE_1_VAL)
            .isEnabled(true)
            .build();
    HttpResponse httpResponse = createHttpResponse(expectedResult, 200);

    when(httpClient.send(any(HttpRequest.class))).thenReturn(httpResponse);

    SecretModel actualResult = secretClient.retrieveSecret(SECRET_KEY_1_VAL);

    assertEquals(expectedResult, actualResult);
  }

  @Test
  public void exceptionRetrieveSecretTest() {
    HttpResponse httpResponse = new HttpResponse();
    httpResponse.setResponseCode(404);

    when(httpClient.send(any(HttpRequest.class))).thenReturn(httpResponse);

    assertThrows(AppException.class, () -> secretClient.retrieveSecret(SECRET_KEY_1_VAL));
  }

  @Test
  public void updateSecretTest() {
    SecretModel expectedResult =
        SecretModel.builder()
            .id(SECRET_KEY_1_VAL)
            .value(SECRET_VALUE_2_VAL)
            .isEnabled(true)
            .build();
    HttpResponse httpResponse = createHttpResponse(expectedResult, 200);

    when(httpClient.send(any(HttpRequest.class))).thenReturn(httpResponse);

    SecretModel actualResult = secretClient.updateSecret(SECRET_KEY_1_VAL, SECRET_VALUE_2_VAL);

    assertEquals(expectedResult, actualResult);
  }

  @Test
  public void exceptionUpdateSecretTest() {
    HttpResponse httpResponse = new HttpResponse();
    httpResponse.setResponseCode(404);

    when(httpClient.send(any(HttpRequest.class))).thenReturn(httpResponse);

    assertThrows(
        AppException.class, () -> secretClient.updateSecret(SECRET_KEY_1_VAL, SECRET_VALUE_2_VAL));
  }

  @Test
  public void removeSecretTest() {
    HttpResponse httpResponse = new HttpResponse();
    httpResponse.setResponseCode(200);

    when(httpClient.send(any(HttpRequest.class))).thenReturn(httpResponse);

    secretClient.removeSecret(SECRET_KEY_1_VAL);
  }

  @Test
  public void exceptionRemoveSecretTest() {
    HttpResponse httpResponse = new HttpResponse();
    httpResponse.setResponseCode(404);

    when(httpClient.send(any(HttpRequest.class))).thenReturn(httpResponse);

    assertThrows(AppException.class, () -> secretClient.removeSecret(SECRET_KEY_1_VAL));
  }

  private HttpResponse createHttpResponse(SecretModel secretModel, int responseCode) {
    HttpResponse httpResponse = new HttpResponse();
    httpResponse.setBody((new Gson()).toJson(secretModel));
    httpResponse.setResponseCode(responseCode);
    return httpResponse;
  }
}
