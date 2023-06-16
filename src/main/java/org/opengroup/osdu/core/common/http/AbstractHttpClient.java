// Copyright 2017-2019, Schlumberger
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.opengroup.osdu.core.common.http;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

abstract class AbstractHttpClient implements IHttpClient {

  private static final Set<String> DISALLOWED_HEADERS_SET;

  static {
    DISALLOWED_HEADERS_SET = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    DISALLOWED_HEADERS_SET.addAll(
        Set.of("connection", "content-length", "expect", "host", "upgrade"));
  }

  @Override
  public HttpResponse send(HttpRequest request) {

    HttpResponse output = new HttpResponse();
    output.setRequest(request);
    try {
      request.setUrl(encodeUrl(request.getUrl()).toString());

      URL url = new URL(request.getUrl());

      Builder httpRequstBuilder = java.net.http.HttpRequest.newBuilder()
          .uri(encodeUrl(request.getUrl()));

      for (Map.Entry<String, String> header : request.headers.entrySet()) {
        if (!DISALLOWED_HEADERS_SET.contains(header.getKey().toLowerCase())) {
          httpRequstBuilder.header(header.getKey(), header.getValue());
        }
      }

      if (request.httpMethod.equals(HttpRequest.POST) ||
          request.httpMethod.equals(HttpRequest.PUT) ||
          request.httpMethod.equals(HttpRequest.PATCH)) {
        BodyPublisher bodyPublisher;
        if (Objects.nonNull(request.getBody())) {
          bodyPublisher = BodyPublishers.ofString(request.getBody());
        } else {
          bodyPublisher = BodyPublishers.noBody();
        }
        httpRequstBuilder.method(request.httpMethod, bodyPublisher);
      } else {
        BodyPublisher bodyPublisher = BodyPublishers.noBody();
        httpRequstBuilder.method(request.httpMethod, bodyPublisher);
      }

      HttpClient httpClient = buildClient(request);

      BodyHandler<String> stringBodyHandler = BodyHandlers.ofString();

      long start = System.currentTimeMillis();

      java.net.http.HttpResponse<String> httpResponse = httpClient.send(
          httpRequstBuilder.build(),
          stringBodyHandler
      );

      HttpHeaders headers = httpResponse.headers();
      headers.firstValue("content-type").ifPresent(output::setContentType);
      output.setResponseCode(httpResponse.statusCode());
      output.setHeaders(headers.map());
      output.setBody(httpResponse.body());
      output.setLatency(System.currentTimeMillis() - start);

    } catch (IOException e) {
      System.err.println(
          String.format("Unexpected error sending to URL %s METHOD %s. error %s", request.url,
              request.httpMethod, e));
      output.setException(e);
    } catch (InterruptedException e) {
      output.setException(e);
      Thread.currentThread().interrupt();
    }
    return output;
  }

  HttpClient buildClient(HttpRequest request) {
    HttpClient.Builder httpClientBuilder = HttpClient.newBuilder();
    httpClientBuilder.followRedirects(request.followRedirects ? Redirect.ALWAYS : Redirect.NEVER);
    httpClientBuilder.connectTimeout(
        Duration.of(request.getConnectionTimeout(), ChronoUnit.MILLIS));
    return httpClientBuilder.build();
  }

  private URI encodeUrl(String url){
    UriComponents uriComponents = UriComponentsBuilder.fromUriString(url).build();
    String uriString = uriComponents.toUriString();
    return URI.create(uriString);
  }

}
