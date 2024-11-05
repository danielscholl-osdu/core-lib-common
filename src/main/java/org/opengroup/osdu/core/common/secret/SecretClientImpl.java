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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.opengroup.osdu.core.common.http.HttpRequest;
import org.opengroup.osdu.core.common.http.HttpResponse;
import org.opengroup.osdu.core.common.http.IHttpClient;
import org.opengroup.osdu.core.common.model.entitlements.Acl;
import org.opengroup.osdu.core.common.model.http.AppException;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.secret.gson.OffsetDateTimeAdapter;
import org.opengroup.osdu.core.common.util.IServiceAccountJwtClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Slf4j
@RequiredArgsConstructor
public class SecretClientImpl implements SecretClient {

  private static final String HTTP_CODE_STRING = "HTTP code = %s.";
  private static final String INTERNAL_SERVER_ERROR_STRING = "Internal Server Error.";

  private final SecretAPIConfig config;
  private final IHttpClient httpClient;
  private final DpsHeaders dpsHeaders;
  private final IServiceAccountJwtClient serviceAccountJwtClient;
  private final AccessGroups accessGroups;

  private final Gson gson =
      new GsonBuilder()
          .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
          .create();

  @Override
  public SecretModel retrieveSecret(String secretKey) {
    String url = createUrl(secretKey);
    Map<String, String> headers = buildHttpHeaders();
    HttpRequest request = HttpRequest.get().url(url).headers(headers).build();

    HttpResponse response = httpClient.send(request);

    log.debug(HTTP_CODE_STRING.formatted(response.getResponseCode()));

    checkHttpResponse(response.getResponseCode());

    return getSecretModel(response);
  }

  @Override
  public SecretModel createSecret(String secretKey, String secretValue) {
    String url = createUrl();
    SecretModel secret =
        SecretModel.builder()
            .id(secretKey)
            .value(secretValue)
            .isEnabled(true)
            .secretAcls(buildAcl())
            .build();
    Map<String, String> headers = buildHttpHeaders();
    HttpRequest request = HttpRequest.post(secret).url(url).headers(headers).build();

    HttpResponse response = httpClient.send(request);

    log.debug(HTTP_CODE_STRING.formatted(response.getResponseCode()));

    return getSecretModel(response);
  }

  private Acl buildAcl() {
    String partitionId = dpsHeaders.getPartitionId();
    String domain = config.getDomain();

    String ownersAcl =
        String.format("%s@%s.%s", accessGroups.accessOwnersGroup(), partitionId, domain);

    String viewersAcl =
        String.format("%s@%s.%s", accessGroups.accessViewersGroup(), partitionId, domain);

    return new Acl(new String[] {viewersAcl}, new String[] {ownersAcl});
  }

  @Override
  public SecretModel updateSecret(String secretKey, String secretValue) {
    String url = createUrl(secretKey);
    Map<String, String> headers = buildHttpHeaders();
    SecretModel secret =
        SecretModel.builder()
            .id(secretKey)
            .value(secretValue)
            .secretAcls(buildAcl())
            .isEnabled(true)
            .build();
    HttpRequest request = HttpRequest.patch(secret).url(url).headers(headers).build();

    HttpResponse response = httpClient.send(request);

    checkHttpResponse(response.getResponseCode());

    log.debug(HTTP_CODE_STRING.formatted(response.getResponseCode()));

    return getSecretModel(response);
  }

  private void checkHttpResponse(int responseCode) {
    if (responseCode != HttpStatus.SC_OK && responseCode != HttpStatus.SC_NO_CONTENT) {
      throw new AppException(
          HttpStatus.SC_INTERNAL_SERVER_ERROR,
          INTERNAL_SERVER_ERROR_STRING,
          INTERNAL_SERVER_ERROR_STRING);
    }
  }

  @Override
  public void removeSecret(String secretKey) {
    String url = createUrl(secretKey);
    Map<String, String> headers = buildHttpHeaders();
    HttpRequest request = HttpRequest.delete().url(url).headers(headers).build();

    HttpResponse response = httpClient.send(request);

    log.debug(HTTP_CODE_STRING.formatted(response.getResponseCode()));

    checkHttpResponse(response.getResponseCode());
  }

  private String createUrl() {
    return createUrl(null);
  }

  private String createUrl(String secretName) {
    String path =
        (secretName == null || secretName.isEmpty())
            ? "/secrets"
            : String.format("/secrets/%s", secretName);
    return URI.create(config.getSecretApi() + path).normalize().toString();
  }

  private Map<String, String> buildHttpHeaders() {
    Map<String, String> headers = new HashMap<>();

    headers.put(DpsHeaders.DATA_PARTITION_ID, dpsHeaders.getPartitionId());
    headers.put(
        HttpHeaders.AUTHORIZATION, serviceAccountJwtClient.getIdToken(dpsHeaders.getPartitionId()));
    headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    return headers;
  }

  private SecretModel getSecretModel(HttpResponse response) {
    try {
      return gson.fromJson(response.getBody(), SecretModel.class);
    } catch (Exception e) {
      throw new AppException(
          HttpStatus.SC_INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_STRING, e.getMessage(), e);
    }
  }
}
