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

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.model.http.AppException;
import org.opengroup.osdu.core.common.model.http.HttpResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({HttpClients.class, OutputStream.class, DpsHeaders.class})
public class UrlFetchServiceImplTest {

    private static final String PUT = "PUT";
    private static final String POST = "POST";
    private static final String GET = "GET";
    private static final String HEADER_NAME = "ANY_HEADER";
    private static final String HEADER_VALUE = "ANY_VALUE";
    private static final String ADDRESS = "http://test.com";
    private static final String BODY = "any http body";
    private static final String RESPONSE = "hello world";

    @InjectMocks
    private UrlFetchServiceImpl sut;

    @Mock
    private HttpClientHandler httpClientHandler;

    @Mock
    private static DpsHeaders HEADERS;

    @Captor
    private ArgumentCaptor<HttpGet> httpGetArgumentCaptor;

    @Before
    public void setup() {
        HEADERS.put(HEADER_NAME, HEADER_VALUE);
    }

    @Test
    public void should_returnResponse_when_getRequestIsSentSuccessfully() throws Exception {

        InputStream stream = new ByteArrayInputStream(RESPONSE.getBytes(StandardCharsets.UTF_8));

        StatusLine statusLine = mock(StatusLine.class);

        HttpEntity entity = mock(HttpEntity.class);

        CloseableHttpResponse response = mock(CloseableHttpResponse.class);

        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);

        HttpResponse httpResponse = mock(HttpResponse.class);
        when(httpResponse.getResponseCode()).thenReturn(200);
        when(httpResponse.getBody()).thenReturn(RESPONSE);

        when(httpClientHandler.sendRequest(any(), any())).thenReturn(httpResponse);

        FetchServiceHttpRequest request = FetchServiceHttpRequest.builder().httpMethod(GET).url(ADDRESS).headers(HEADERS).build();
        HttpResponse result = this.sut.sendRequest(request);
        assertEquals(HttpStatus.SC_OK, result.getResponseCode());
        assertEquals(RESPONSE, result.getBody());
    }

    @Test
    public void shouldReturnResponseSuccessfullyWhenRequestIsValid() throws URISyntaxException {
        final FetchServiceHttpRequest httpRequest = FetchServiceHttpRequest.builder().httpMethod(GET).url(ADDRESS).headers(HEADERS).build();
        final HttpResponse expectedHttpResponse = new HttpResponse();
        when(httpClientHandler.sendRequest(Mockito.any(HttpGet.class), Mockito.eq(HEADERS))).thenReturn(expectedHttpResponse);

        final HttpResponse httpResponse = sut.sendRequest(httpRequest);

        assertEquals(expectedHttpResponse, httpResponse);
        verify(httpClientHandler).sendRequest(httpGetArgumentCaptor.capture(), Mockito.eq(HEADERS));
        assertEquals(httpGetArgumentCaptor.getValue().getURI().toString(), ADDRESS);
    }

    @Test
    public void should_returnHttp404_when_httpMethodIsInvalid() {

        try {
            FetchServiceHttpRequest request = FetchServiceHttpRequest.builder().httpMethod("DELETE").url(ADDRESS).headers(HEADERS).body(BODY).build();
            this.sut.sendRequest(request);

            fail("Should not succeed");
        } catch (AppException e) {
            assertEquals(HttpStatus.SC_NOT_FOUND, e.getError().getCode());
            assertEquals("Invalid HTTP method", e.getError().getReason());
            assertEquals("Invalid HTTP method", e.getError().getMessage());
        } catch (Exception e) {
            fail("Should not get different exception");
        }
    }

    @Test
    public void should_returnResponse_when_postRequestIsSentSuccessfully() throws Exception {

        InputStream stream = new ByteArrayInputStream(RESPONSE.getBytes(StandardCharsets.UTF_8));

        StatusLine statusLine = mock(StatusLine.class);

        HttpEntity entity = mock(HttpEntity.class);

        CloseableHttpResponse response = mock(CloseableHttpResponse.class);

        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);


        HttpResponse httpResponse = mock(HttpResponse.class);
        when(httpResponse.getResponseCode()).thenReturn(200);
        when(httpResponse.getBody()).thenReturn(RESPONSE);

        when(httpClientHandler.sendRequest(any(), any())).thenReturn(httpResponse);

        FetchServiceHttpRequest request = FetchServiceHttpRequest.builder().httpMethod(POST).url(ADDRESS).headers(HEADERS).body(BODY).build();
        HttpResponse result = this.sut.sendRequest(request);
        assertEquals(HttpStatus.SC_OK, result.getResponseCode());
        assertEquals(RESPONSE, result.getBody());
    }

    @Test
    public void should_returnTrue_when_httpStatusCodeIsBetween200And204() {
        HttpResponse response = new HttpResponse();
        response.setResponseCode(HttpStatus.SC_OK);
        assertTrue(response.isSuccessCode());

        response.setResponseCode(HttpStatus.SC_CREATED);
        assertTrue(response.isSuccessCode());

        response.setResponseCode(HttpStatus.SC_ACCEPTED);
        assertTrue(response.isSuccessCode());

        response.setResponseCode(HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION);
        assertTrue(response.isSuccessCode());

        response.setResponseCode(HttpStatus.SC_NO_CONTENT);
        assertTrue(response.isSuccessCode());
    }

    @Test
    public void should_returnFalse_when_httpStatusCodeIsLesserThan200AndBiggerThan204() {
        HttpResponse response = new HttpResponse();
        response.setResponseCode(HttpStatus.SC_CONTINUE);
        assertFalse(response.isSuccessCode());

        response.setResponseCode(HttpStatus.SC_SWITCHING_PROTOCOLS);
        assertFalse(response.isSuccessCode());

        response.setResponseCode(HttpStatus.SC_BAD_REQUEST);
        assertFalse(response.isSuccessCode());

        response.setResponseCode(HttpStatus.SC_FORBIDDEN);
        assertFalse(response.isSuccessCode());

        response.setResponseCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        assertFalse(response.isSuccessCode());
    }

    @Test
    public void should_returnNull_when_responseBodyIsNull() {
        HttpResponse response = new HttpResponse();

        assertNull(response.getAsJsonObject());
    }

    @Test
    public void should_returnResponseBodyInJsonFormat_when_responseBodyIsNotNull() {
        final String BODY = "{\"status\":200}";

        HttpResponse response = new HttpResponse();
        response.setBody(BODY);

        assertEquals(BODY, response.getAsJsonObject().toString());
    }

    @Test
    public void should_returnResponse_when_putRequestIsSentSuccessfully() throws Exception {

        InputStream stream = new ByteArrayInputStream(RESPONSE.getBytes(StandardCharsets.UTF_8));

        StatusLine statusLine = mock(StatusLine.class);

        HttpEntity entity = mock(HttpEntity.class);

        CloseableHttpResponse response = mock(CloseableHttpResponse.class);

        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);


        HttpResponse httpResponse = mock(HttpResponse.class);
        when(httpResponse.getResponseCode()).thenReturn(200);
        when(httpResponse.getBody()).thenReturn(RESPONSE);

        when(httpClientHandler.sendRequest(any(), any())).thenReturn(httpResponse);

        FetchServiceHttpRequest request = FetchServiceHttpRequest.builder().httpMethod(PUT).url(ADDRESS).headers(HEADERS).body(BODY).build();
        HttpResponse result = this.sut.sendRequest(request);
        assertEquals(HttpStatus.SC_OK, result.getResponseCode());
        assertEquals(RESPONSE, result.getBody());
    }
}