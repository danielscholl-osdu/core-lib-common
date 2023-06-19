// Copyright 2017-2023, Schlumberger
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

package org.opengroup.osdu.core.common.crs;

import com.google.gson.Gson;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.opengroup.osdu.core.common.http.HttpResponse;
import org.opengroup.osdu.core.common.http.json.HttpResponseBodyMapper;
import org.opengroup.osdu.core.common.http.json.HttpResponseBodyParsingException;
import org.opengroup.osdu.core.common.model.crs.ConvertGeoJsonRequest;
import org.opengroup.osdu.core.common.model.crs.ConvertGeoJsonResponse;
import org.opengroup.osdu.core.common.model.crs.ConvertPointsRequest;
import org.opengroup.osdu.core.common.model.crs.ConvertPointsResponse;
import org.opengroup.osdu.core.common.model.crs.ConvertTrajectoryRequest;
import org.opengroup.osdu.core.common.model.crs.ConvertTrajectoryResponse;
import org.opengroup.osdu.core.common.model.crs.CrsConverterException;
import org.opengroup.osdu.core.common.model.http.AppException;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.model.http.RequestStatus;
import org.opengroup.osdu.core.common.util.UrlNormalizationUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;

public class CrsConverterService implements ICrsConverterService {
    private static final String CONVERT_POINTS_PATH = "/convert";
    private static final String CONVERT_TRAJECTORY_PATH = "/convertTrajectory";
    private static final String CONVERT_GEO_JSON_PATH = "/convertGeoJson";

    private final String rootUrl;
    private final CloseableHttpClient httpClient;
    private final DpsHeaders headers;
    private final HttpResponseBodyMapper responseBodyMapper;

    private final Gson gson = new Gson();

    public CrsConverterService(CrsConverterAPIConfig config,
                               CloseableHttpClient httpClient,
                               DpsHeaders headers,
                               HttpResponseBodyMapper mapper) {
        this.rootUrl = config.getRootUrl();
        this.httpClient = httpClient;
        this.headers = headers;
        this.responseBodyMapper = mapper;
        if (config.apiKey != null) {
            headers.put("AppKey", config.apiKey);
        }
    }

    @Override
    public ConvertPointsResponse convertPoints(ConvertPointsRequest request) throws CrsConverterException {
        HttpResponse response = send(request, CONVERT_POINTS_PATH);
        return this.getResult(response, ConvertPointsResponse.class);
    }

    @Override
    public ConvertTrajectoryResponse convertTrajectory(ConvertTrajectoryRequest request) throws CrsConverterException {
        HttpResponse response = send(request, CONVERT_TRAJECTORY_PATH);
        return this.getResult(response, ConvertTrajectoryResponse.class);
    }

    @Override
    public ConvertGeoJsonResponse convertGeoJson(ConvertGeoJsonRequest request) throws CrsConverterException {
        HttpResponse response = send(request, CONVERT_GEO_JSON_PATH);
        return this.getResult(response, ConvertGeoJsonResponse.class);
    }

     private <T> HttpResponse send(T request, String path) throws CrsConverterException {
        String url = this.createUrl(path);
        HttpPost httpRequest = new HttpPost(url);
        StringEntity entity;
        try {
            entity = new StringEntity(gson.toJson(request));
        } catch (UnsupportedEncodingException e) {
            throw new CrsConverterException(String.format("Error making request to CrsConverter service, error: %s", e.getMessage()), null);
        }
        httpRequest.setEntity(entity);
        headers.getHeaders().forEach(httpRequest::addHeader);
        try {
            try (CloseableHttpResponse response = httpClient.execute(httpRequest)) {
                HttpResponse output = new HttpResponse();
                output.setResponseCode(response.getStatusLine().getStatusCode());
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

    private CrsConverterException generateException(HttpResponse result) {
        return new CrsConverterException(
                "Error making request to CrsConverter service. Check the inner HttpResponse for more info.", result);
    }

    private String createUrl(String pathAndQuery) {
        return UrlNormalizationUtil.normalizeStringUrl(this.rootUrl, pathAndQuery);
    }

    private <T> T getResult(HttpResponse result, Class<T> type) throws CrsConverterException {
        if (result.isSuccessCode()) {
            try {
                return responseBodyMapper.parseBody(result, type);
            } catch (HttpResponseBodyParsingException e) {
                throw new CrsConverterException("Error parsing response. Check the inner HttpResponse for more info.",
                        result);
            }
        } else {
            throw this.generateException(result);
        }
    }
}