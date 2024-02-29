// Copyright 2017-2020, Schlumberger
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

package org.opengroup.osdu.core.common.partition;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.opengroup.osdu.core.common.http.HttpResponse;
import org.opengroup.osdu.core.common.model.http.AppException;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.model.http.RequestStatus;
import org.opengroup.osdu.core.common.util.UrlNormalizationUtil;

public class PartitionService implements IPartitionProvider {
    private final String rootUrl;
    private final DpsHeaders headers;
    CloseableHttpClient cacheHttpClient;

    private final Gson gson = new Gson();

    public PartitionService(PartitionAPIConfig config,
                            DpsHeaders headers,
                            CloseableHttpClient cacheHttpClient) {
        // Normalize the root URL to verify that the root URL is correct.
        this.rootUrl = UrlNormalizationUtil.normalizeStringUrl(config.getRootUrl());
        this.headers = headers;
        this.cacheHttpClient = cacheHttpClient;
    }

    @Override
    public PartitionInfo get(String name) throws PartitionException {
        String url = this.createUrl(String.format("/partitions/%s", name));
        HttpGet httpGetRequest = new HttpGet(url);
        HttpResponse response = send(httpGetRequest);
        Map<String, Property> properties = getResult(response,Map.class,String.class,Property.class);
        return PartitionInfo
                .builder()
                .properties(properties)
                .build();
    }

    @Override
    public PartitionInfo create(String partitionId, PartitionInfo partitionInfo) throws PartitionException {
        String url = this.createUrl(String.format("/partitions/%s", partitionId));
        HttpPost httpPost = new HttpPost(url);
        StringEntity entity;
        try {
            String jsonString = this.gson.toJson(partitionInfo);
            entity = new StringEntity(jsonString);
        } catch (UnsupportedEncodingException e) {
            throw new PartitionException(
                    String.format("Error making request to Partition service create api, error: %s", e.getMessage()), null);
        }
        httpPost.setEntity(entity);
        HttpResponse response = send(httpPost);
        Map<String, Property> properties = getResult(response,Map.class,String.class,Property.class);
        return PartitionInfo
                .builder()
                .properties(properties)
                .build();
    }

    @Override
    public void update(String partitionId, PartitionInfo partitionInfo) throws PartitionException {
        String url = this.createUrl(String.format("/partitions/%s", partitionId));
        HttpPatch httpPatch = new HttpPatch(url);
        StringEntity entity;
        try {
            String jsonString = this.gson.toJson(partitionInfo);
            entity = new StringEntity(jsonString);
        } catch (UnsupportedEncodingException e) {
            throw new PartitionException(
                    String.format("Error making request to Partition service update api, error: %s", e.getMessage()), null);
        }
        httpPatch.setEntity(entity);
        HttpResponse response = send(httpPatch);
        getResult(response, String.class);
    }

    @Override
    public void delete(String partitionId) throws PartitionException {
        String url = this.createUrl(String.format("/partitions/%s", partitionId));
        HttpDelete httpDelete = new HttpDelete(url);
        HttpResponse response = send(httpDelete);
        getResult(response, String.class);
    }

    @Override
    public List<String> list() throws PartitionException {
        String url = this.createUrl("/partitions");
        HttpGet httpGetRequest = new HttpGet(url);
        HttpResponse response = send(httpGetRequest);
        List<String> partitions = getResult(response, List.class);
        return partitions;
    }

    private HttpResponse send(HttpRequestBase request) throws PartitionException {
        Map<String, String> dpsHeader = this.headers.getHeaders();
        request.addHeader(DpsHeaders.AUTHORIZATION, dpsHeader.get(DpsHeaders.AUTHORIZATION));
        request.addHeader(DpsHeaders.CONTENT_TYPE, dpsHeader.get(DpsHeaders.CONTENT_TYPE));
        request.addHeader(DpsHeaders.CORRELATION_ID, dpsHeader.get(DpsHeaders.CORRELATION_ID));
        try {
            try (CloseableHttpResponse response = cacheHttpClient.execute(request)) {
                HttpResponse output = new HttpResponse();
                output.setResponseCode(response.getStatusLine().getStatusCode());
                if (response.getStatusLine().getStatusCode() == 204) {
                    return output;
                }
                StringBuilder responseBuilder = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
                    String responsePayloadLine;
                    while ((responsePayloadLine = br.readLine()) != null) {
                        responseBuilder.append(responsePayloadLine);
                    }
                }
                String responseBody = responseBuilder.toString();
                output.setBody(responseBody);
                return output;
            }
        } catch (SocketTimeoutException e) {
            throw new AppException(RequestStatus.SOCKET_TIMEOUT, "Socket time out", "Request cannot be completed in specified time", e);
        } catch (IOException e) {
            throw new AppException(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Internal communication failure", "Internal communication failure", e);
        }
    }

    private <T> T getResult(HttpResponse result, Class<T> type, Class... params) throws PartitionException {
        if (!result.isSuccessCode()) {
            throw this.generatePartitionException(result);
        }

        try {
            if (StringUtils.isBlank(result.getBody())) {
                return null;
            }
            if(params.length > 0){
                Type parametrizedType = TypeToken.getParameterized(type, params).getType();
                return gson.fromJson(result.getBody(), parametrizedType);
            }
            return gson.fromJson(result.getBody(), type);
        } catch (JsonSyntaxException e) {
            throw new PartitionException(String.format("Error parsing response. With message: %s Check the inner HttpResponse for more info.",e.getMessage()),
                result);
        }
    }

    private String createUrl(String pathAndQuery) throws PartitionException {
        try {
            return UrlNormalizationUtil.normalizeStringUrl(this.rootUrl, pathAndQuery);
        } catch (IllegalArgumentException illegalArgumentException) {
            HttpResponse response = new HttpResponse();
            response.setBody(illegalArgumentException.getMessage());
            response.setResponseCode(400);
            throw new PartitionException(String.format("Invalid partition path: %s", pathAndQuery), response);
        }
    }

    private PartitionException generatePartitionException(HttpResponse result) {
        return new PartitionException(
                "Error making request to Partition service. Check the inner HttpResponse for more info.", result);
    }
}