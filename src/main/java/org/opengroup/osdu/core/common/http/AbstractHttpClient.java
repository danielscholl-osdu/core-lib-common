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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
abstract class AbstractHttpClient implements IHttpClient {

    @Override
    public HttpResponse send(HttpRequest request) {

        HttpResponse output = new HttpResponse();
        output.setRequest(request);
        try {

            URI uri = new URIBuilder(encodeUrl(request.getUrl())).build();

            if (isRequestMethodWithBody(request) && Objects.isNull(request.getBody())){
                request.setBody("");
            }

            HttpRequestBase requestBase = isRequestMethodWithBody(request)
                ? getBodyHttpBase(request, uri)
                : getNoBodyHttpBase(request, uri);

            try (CloseableHttpClient httpclient = getHttpClient(request)) {

                long start = System.currentTimeMillis();
                CloseableHttpResponse response = httpclient.execute(requestBase);
                HttpEntity entity = response.getEntity();
                if (Objects.nonNull(entity)) {
                    StringBuilder responseBuilder = new StringBuilder();
                    try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(entity.getContent()))) {
                        String responsePayloadLine;
                        while ((responsePayloadLine = br.readLine()) != null) {
                            responseBuilder.append(responsePayloadLine);
                        }
                    }
                    String responseBody = responseBuilder.toString();
                    output.setContentType(
                        ContentType.getOrDefault(response.getEntity()).getMimeType());
                    output.setBody(responseBody);
                }
                Header[] allHeaders = response.getAllHeaders();
                HashMap<String, List<String>> headersMap = new HashMap<>();
                for (Header header : allHeaders) {
                    headersMap.put(header.getName(), Collections.singletonList(header.getValue()));
                }
                output.setResponseCode(response.getStatusLine().getStatusCode());
                output.setLatency(System.currentTimeMillis() - start);
                output.setHeaders(headersMap);
            }

        } catch (URISyntaxException | IOException e) {
            log.error("Unexpected error sending to URL {} METHOD {} error {}", request.url,
                request.httpMethod, e);
            output.setException(e);
        }
        return output;
    }

    CloseableHttpClient getHttpClient(HttpRequest request) {
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(request.connectionTimeout)
            .setRedirectsEnabled(request.followRedirects)
            .setConnectionRequestTimeout(request.connectionTimeout)
            .setSocketTimeout(request.connectionTimeout)
            .build();

        List<Header> httpHeaders = new ArrayList<>();
        for (String key : request.getHeaders().keySet()) {
            httpHeaders.add(new BasicHeader(key, request.getHeaders().get(key)));
        }
        if (!request.getHeaders().containsKey(HttpHeaders.ACCEPT)) {
            httpHeaders.add(
                new BasicHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString()));
        }

        return HttpClients.custom()
            .setDefaultHeaders(httpHeaders)
            .setDefaultRequestConfig(requestConfig)
            .build();
    }

    private String encodeUrl(String url) {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(url).build();
        return uriComponents.toUriString();
    }

    private HttpRequestBase getBodyHttpBase(HttpRequest request, URI uri)
        throws ClientProtocolException {
        StringEntity stringEntity = new StringEntity(request.body, StandardCharsets.UTF_8);
        switch (request.httpMethod) {
            case HttpRequest.POST:
                HttpPost post = new HttpPost(uri);
                post.setEntity(stringEntity);
                return post;
            case HttpRequest.PUT:
                HttpPut put = new HttpPut(uri);
                put.setEntity(stringEntity);
                return put;
            case HttpRequest.PATCH:
                HttpPatch httpPatch = new HttpPatch(uri);
                httpPatch.setEntity(stringEntity);
                return httpPatch;
            default:
                throw new ClientProtocolException("Invalid HTTP method: " + request.httpMethod);
        }
    }

    private static HttpRequestBase getNoBodyHttpBase(HttpRequest request, URI uri)
        throws ClientProtocolException {
        switch (request.httpMethod) {
            case HttpRequest.GET:
                return new HttpGet(uri);
            case HttpRequest.DELETE:
                return new HttpDelete(uri);
            case HttpRequest.HEAD:
                return new HttpHead(uri);
            default:
                throw new ClientProtocolException("Invalid HTTP method: " + request.httpMethod);
        }
    }

    private static boolean isRequestMethodWithBody(HttpRequest request) {
        return request.httpMethod.equals(HttpRequest.POST) ||
            request.httpMethod.equals(HttpRequest.PUT) ||
            request.httpMethod.equals(HttpRequest.PATCH);
    }
}
