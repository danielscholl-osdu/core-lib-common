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

package org.opengroup.osdu.core.common.legal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opengroup.osdu.core.common.http.HttpRequest;
import org.opengroup.osdu.core.common.http.HttpResponse;
import org.opengroup.osdu.core.common.http.IHttpClient;
import org.opengroup.osdu.core.common.http.json.HttpResponseBodyMapper;
import org.opengroup.osdu.core.common.http.json.HttpResponseBodyParsingException;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.model.legal.InvalidTagsWithReason;
import org.opengroup.osdu.core.common.model.legal.LegalException;
import org.opengroup.osdu.core.common.model.legal.LegalTag;
import org.opengroup.osdu.core.common.model.legal.LegalTagProperties;
import org.opengroup.osdu.core.common.model.legal.RequestLegalTags;
import org.opengroup.osdu.core.common.util.UrlNormalizationUtil;

public class LegalService implements ILegalProvider {
    private final String rootUrl;
    private final IHttpClient httpClient;
    private final DpsHeaders headers;
    private final ObjectMapper objectMapper;
    private final HttpResponseBodyMapper responseBodyMapper;

    LegalService(LegalAPIConfig config,
                 IHttpClient httpClient,
                 DpsHeaders headers,
                 HttpResponseBodyMapper mapper) {
        this.rootUrl = config.getRootUrl();
        this.httpClient = httpClient;
        this.headers = headers;
        this.objectMapper = new ObjectMapper();
        this.responseBodyMapper = mapper;
        if (config.apiKey != null) {
            headers.put("AppKey", config.apiKey);
        }
    }

    @Override
    public LegalTag create(LegalTag lt) throws LegalException {
        String body;
        try {
            body = objectMapper.writeValueAsString(lt);
        } catch (JsonProcessingException e) {
            throw new LegalException("Cannot build request from legal tag", null);
        }
        String url = this.createUrl("/legaltags");
        HttpResponse result = this.httpClient.send(
                HttpRequest.post().body(body).url(url).headers(this.headers.getHeaders()).build());
        return this.getResult(result, LegalTag.class);
    }

    @Override
    public LegalTag get(String name) throws LegalException {
        String url = this.createUrl(String.format("/legaltags/%s", name));
        HttpResponse result = this.httpClient.send(
                HttpRequest.get().url(url).headers(this.headers.getHeaders()).build());
        return result.IsNotFoundCode() ? null : this.getResult(result, LegalTag.class);
    }

    @Override
    public void delete(String name) throws LegalException {
        String url = this.createUrl(String.format("/legaltags/%s", name));
        HttpResponse result = this.httpClient.send(
                HttpRequest.delete().url(url).headers(this.headers.getHeaders()).build());
        this.getResult(result, String.class);
    }

    @Override
    public InvalidTagsWithReason validate(String... names) throws LegalException {
        String url = this.createUrl(String.format("/legaltags:validate"));
        RequestLegalTags rlt = new RequestLegalTags();
        rlt.setNames(names);
        HttpResponse result = this.httpClient.send(
                HttpRequest.post(rlt).url(url).headers(this.headers.getHeaders()).build());
        return this.getResult(result, InvalidTagsWithReason.class);
    }

    @Override
    public LegalTagProperties getLegalTagProperties() throws LegalException {
        String url = this.createUrl("/legaltags:properties");
        HttpResponse result = this.httpClient.send(
                HttpRequest.get().url(url).headers(this.headers.getHeaders()).build());
        return result.IsNotFoundCode() ? null : this.getResult(result, LegalTagProperties.class);
    }

    private LegalException generateException(HttpResponse result) {
        return new LegalException(
                "Error making request to Legal service. Check the inner HttpResponse for more info.", result);
    }

    private String createUrl(String pathAndQuery) {
        return UrlNormalizationUtil.normalizeStringUrl(this.rootUrl,pathAndQuery);
    }

    private <T> T getResult(HttpResponse result, Class<T> type) throws LegalException {
        if (result.isSuccessCode()) {
            try {
                return responseBodyMapper.parseBody(result, type);
            } catch (HttpResponseBodyParsingException e) {
                throw new LegalException("Error parsing response. Check the inner HttpResponse for more info.", result);
            }
        } else {
            throw this.generateException(result);
        }
    }
}