package org.opengroup.osdu.core.common.search;

import org.mockito.ArgumentCaptor;
import org.opengroup.osdu.core.common.http.HttpClient;
import org.opengroup.osdu.core.common.http.HttpRequest;
import org.opengroup.osdu.core.common.http.HttpResponse;
import org.opengroup.osdu.core.common.http.IHttpClient;
import org.opengroup.osdu.core.common.http.json.HttpResponseBodyMapper;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.model.search.*;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SearchServiceTest {

    public static final String ROOT_URL = "http://example.com";

    private IHttpClient httpClient;

    private SearchAPIConfig config;

    private DpsHeaders dpsHeaders;

    private HttpResponseBodyMapper bodyMapper;

    @Before
    public void setup() {
        config = new SearchAPIConfig("url", "any key");
        dpsHeaders = new DpsHeaders();
        httpClient = mock(HttpClient.class);
        bodyMapper = mock(HttpResponseBodyMapper.class);
    }

    @Test
    public void constructorTest() {
        SearchService searchService = new SearchService(config, httpClient, dpsHeaders, bodyMapper);
        assertNotNull(searchService);
    }

    @Test(expected = SearchException.class)
    public void should_throw_Exception_when_searchCursor_response_is_empty() throws SearchException {
        HttpResponse httpResponse = new HttpResponse();
        SearchService searchService = new SearchService(config, httpClient, dpsHeaders, bodyMapper);
        when(httpClient.send(any(HttpRequest.class))).thenReturn(httpResponse);
        searchService.searchCursor(new CursorQueryRequest());
    }

    @Test
    public void should_return_null_when_searchCursor_response_is_invalid() throws SearchException {
        HttpResponse httpResponse = mock(HttpResponse.class);
        SearchService searchService = new SearchService(config, httpClient, dpsHeaders, bodyMapper);
        when(httpResponse.isSuccessCode()).thenReturn(true);
        when(httpClient.send(any(HttpRequest.class))).thenReturn(httpResponse);
        CursorQueryResponse cursorQueryResponse = searchService.searchCursor(new CursorQueryRequest());
        assertNull(cursorQueryResponse);
    }

    @Test
    public void should_return_valid_response_when_searchCursor_response_is_valid() throws Exception {
        HttpResponse httpResponse = mock(HttpResponse.class);
        SearchService searchService = new SearchService(config, httpClient, dpsHeaders, bodyMapper);
        when(httpResponse.isSuccessCode()).thenReturn(true);
        when(bodyMapper.parseBody(httpResponse, CursorQueryResponse.class)).thenReturn(new CursorQueryResponse());
        when(httpClient.send(any(HttpRequest.class))).thenReturn(httpResponse);
        CursorQueryResponse cursorQueryResponse = searchService.searchCursor(new CursorQueryRequest());
        assertNotNull(cursorQueryResponse);
    }

    @Test(expected = SearchException.class)
    public void should_throw_Exception_when_search_response_is_empty() throws SearchException {
        HttpResponse httpResponse = new HttpResponse();
        SearchService searchService = new SearchService(config, httpClient, dpsHeaders, bodyMapper);
        when(httpClient.send(any(HttpRequest.class))).thenReturn(httpResponse);
        searchService.search(new QueryRequest());
    }

    @Test
    public void should_return_null_when_search_response_is_invalid() throws SearchException {
        HttpResponse httpResponse = mock(HttpResponse.class);
        SearchService searchService = new SearchService(config, httpClient, dpsHeaders, bodyMapper);
        when(httpResponse.isSuccessCode()).thenReturn(true);
        when(httpClient.send(any(HttpRequest.class))).thenReturn(httpResponse);
        QueryResponse queryResponse = searchService.search(new QueryRequest());
        assertNull(queryResponse);
    }

    @Test
    public void should_return_valid_response_when_search_response_is_valid() throws Exception {
        HttpResponse httpResponse = mock(HttpResponse.class);
        SearchService searchService = new SearchService(config, httpClient, dpsHeaders, bodyMapper);
        when(httpResponse.isSuccessCode()).thenReturn(true);
        when(bodyMapper.parseBody(httpResponse, QueryResponse.class)).thenReturn(new QueryResponse());
        when(httpClient.send(any(HttpRequest.class))).thenReturn(httpResponse);
        QueryResponse queryResponse = searchService.search(new QueryRequest());
        assertNotNull(queryResponse);
    }

    @Test
    public void testUrlNormalization() throws SearchException {
        HttpResponse response = new HttpResponse();
        response.setResponseCode(200);
        when(httpClient.send(any())).thenReturn(response);
        String malformedRootUrl = " \n  " + ROOT_URL + "\n // \t \f \r";
        config = SearchAPIConfig.builder().rootUrl(malformedRootUrl).build();
        SearchService searchService = new SearchService(config, httpClient, dpsHeaders, bodyMapper);

        searchService.search(new QueryRequest());
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(captor.capture());
        assertEquals(ROOT_URL + "/query",captor.getValue().getUrl());
    }
}