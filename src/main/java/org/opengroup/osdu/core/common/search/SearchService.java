// Copyright 2021 Schlumberger
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

package org.opengroup.osdu.core.common.search;

import org.opengroup.osdu.core.common.http.json.HttpResponseBodyMapper;
import org.opengroup.osdu.core.common.http.json.HttpResponseBodyParsingException;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.model.search.*;
import org.opengroup.osdu.core.common.http.HttpRequest;
import org.opengroup.osdu.core.common.http.HttpResponse;
import org.opengroup.osdu.core.common.http.IHttpClient;
import org.opengroup.osdu.core.common.util.UrlNormalizationUtil;

public class SearchService implements ISearchService {
    public SearchService(SearchAPIConfig config,
                  IHttpClient httpClient,
                  DpsHeaders headers,
                  HttpResponseBodyMapper bodyMapper) {
        this.rootUrl = config.getRootUrl();
        this.httpClient = httpClient;
        this.headers = headers;
        this.bodyMapper = bodyMapper;
        if (config.apiKey != null) {
            headers.put("AppKey", config.getApiKey());
        }
    }

    private final String rootUrl;
    private final IHttpClient httpClient;
    private final DpsHeaders headers;
    private final HttpResponseBodyMapper bodyMapper;

    @Override
    public CursorQueryResponse getAllKindEntries(String kind) throws SearchException {
        CursorQueryRequest request = new CursorQueryRequest();
        request.setKind(kind);
        request.setQuery("");
        return this.searchCursor(request);
    }


    /**
     * Executes a search using the Query API.
     */
    @Override
    public QueryResponse search(QueryRequest searchRequest) throws SearchException {
        String url = this.createUrl("/query");
        HttpResponse result = this.httpClient.send(
                HttpRequest.post(searchRequest).url(url).headers(this.headers.getHeaders()).build());
        return result.IsNotFoundCode() ? new QueryResponse() : this.getResult(result, QueryResponse.class);
    }

    /**
     * Executes a search using the Query with cursor API.
     */
    @Override
    public CursorQueryResponse searchCursor(CursorQueryRequest cursorRequest) throws SearchException {
        String url = this.createUrl("/query_with_cursor");
        HttpResponse result = this.httpClient.send(
                HttpRequest.post(cursorRequest).url(url).headers(this.headers.getHeaders()).build());
        return result.IsNotFoundCode() ? new CursorQueryResponse() : this.getResult(result, CursorQueryResponse.class);
    }

    private SearchException generateException(HttpResponse result) {
        return new SearchException(
                "Error making request to Search service. Check the inner HttpResponse for more info.", result);
    }

    private String createUrl(String pathAndQuery) {
        return UrlNormalizationUtil.normalizeStringUrl(this.rootUrl,pathAndQuery);
    }

    private <T> T getResult(HttpResponse result, Class<T> type) throws SearchException {
        if (result.isSuccessCode()) {
            try {
                return bodyMapper.parseBody(result, type);
            } catch (HttpResponseBodyParsingException e) {
                throw new SearchException("Error parsing response. Check the inner HttpResponse for more info.",
                        result);
            }
        } else {
            throw this.generateException(result);
        }
    }
}
