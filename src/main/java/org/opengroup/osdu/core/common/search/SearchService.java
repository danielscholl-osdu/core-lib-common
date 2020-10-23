package org.opengroup.osdu.core.common.search;

import org.apache.commons.lang3.StringUtils;
import com.google.gson.JsonObject;
import org.opengroup.osdu.core.common.http.json.HttpResponseBodyMapper;
import org.opengroup.osdu.core.common.http.json.HttpResponseBodyParsingException;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.model.search.*;
import org.opengroup.osdu.core.common.http.HttpRequest;
import org.opengroup.osdu.core.common.http.HttpResponse;
import org.opengroup.osdu.core.common.http.IHttpClient;

public class SearchService implements ISearchService {
    SearchService(SearchAPIConfig config,
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

    @Override
    public JsonObject getIndexSchema(String kind) throws SearchException {
        String url = this.createUrl(String.format("/index/schema/%s", kind));
        HttpResponse result = this.httpClient.send(
                HttpRequest.get().url(url).headers(this.headers.getHeaders()).build());
        return result.IsNotFoundCode() ? null : this.getResult(result, JsonObject.class);
    }

    @Override
    public void deleteIndex(String kind) throws SearchException {
        String url = this.createUrl(String.format("/index/%s", kind));
        HttpResponse result = this.httpClient.send(
                HttpRequest.delete().url(url).headers(this.headers.getHeaders()).build());
        this.getResult(result, String.class);
    }

    private SearchException generateException(HttpResponse result) {
        return new SearchException(
                "Error making request to Search service. Check the inner HttpResponse for more info.", result);
    }

    private String createUrl(String pathAndQuery) {
        return StringUtils.join(this.rootUrl, pathAndQuery);
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
