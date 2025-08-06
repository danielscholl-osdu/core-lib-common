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
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.opengroup.osdu.core.common.logging.JaxRsDpsLog;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.model.http.AppException;
import org.opengroup.osdu.core.common.model.http.HttpResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({HttpClients.class})
public class HttpClientHandlerTest {

    private static final String POST = "POST";
    private static final String HEADER_NAME = "ANY_HEADER";
    private static final String HEADER_VALUE = "ANY_VALUE";
    private static final String URL = "http://test.com";
    private static final String RESPONSE = "hello world";

    @InjectMocks
    private HttpClientHandler sut;

    @Mock
    private static DpsHeaders HEADERS;

    @Mock
    private JaxRsDpsLog log;

    @Before
    public void setup() {
        HEADERS.put(HEADER_NAME, HEADER_VALUE);
//        mockStatic(HttpClients.class);
    }


    @Test
    public void should_keepResponseCharsetIntact() throws IOException {
        String specialChar = "ÆÆ";
        InputStream stream = new ByteArrayInputStream(specialChar.getBytes(StandardCharsets.UTF_8));
        String responseBody = this.sut.readResponseBody(stream);
        assertEquals(specialChar, responseBody);
    }

    @Ignore
    @Test
    public void should_returnResponseWithHttp200_when_makingValidRequest() throws IOException, URISyntaxException {

        InputStream stream = new ByteArrayInputStream(RESPONSE.getBytes(StandardCharsets.UTF_8));

        StatusLine statusLine = mock(StatusLine.class);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);

        HttpEntity entity = mock(HttpEntity.class);
        when(entity.getContent()).thenReturn(stream);

        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(response.getEntity()).thenReturn(entity);

        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        when(httpClient.execute(any(HttpPost.class))).thenReturn(response);

        when(HttpClients.createDefault()).thenReturn(httpClient);

        HttpResponse httpResponse = mock(HttpResponse.class);
        when(httpResponse.getResponseCode()).thenReturn(200);
        when(httpResponse.getBody()).thenReturn(RESPONSE);

        ServiceUnavailableRetryStrategy retryStrategy = mock(ServiceUnavailableRetryStrategy.class);
        HttpClientBuilder builder = mock(HttpClientBuilder.class);
        when(HttpClients.custom()).thenReturn(builder);
        when(HttpClients.custom().setServiceUnavailableRetryStrategy(retryStrategy).build()).thenReturn(httpClient);

        HttpRequestBase request = mock(HttpRequestBase.class);
        when(request.getMethod()).thenReturn(POST);
        when(request.getURI()).thenReturn(new URI(URL));

        when(ContentType.getOrDefault(entity)).thenReturn(ContentType.APPLICATION_JSON);

        HttpResponse result = this.sut.sendRequest(request, HEADERS);
        assertEquals(HttpStatus.SC_OK, result.getResponseCode());
        assertEquals(RESPONSE, result.getBody());
    }

    @Ignore
    @Test
    public void should_returnHttp500_when_anIOExceptionOccur() throws Exception {

        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        when(httpClient.execute(any(HttpPost.class))).thenThrow(new IOException("Fatal error"));

        ServiceUnavailableRetryStrategy retryStrategy = mock(ServiceUnavailableRetryStrategy.class);
        HttpClientBuilder builder = mock(HttpClientBuilder.class);
        when(HttpClients.custom()).thenReturn(builder);
        when(HttpClients.custom().setServiceUnavailableRetryStrategy(retryStrategy).build()).thenReturn(httpClient);

        HttpRequestBase request = mock(HttpRequestBase.class);
        when(request.getURI()).thenReturn(new URI(URL));

        try {
            this.sut.sendRequest(request, HEADERS);

            fail("Should not succeed");
        } catch (AppException e) {
            assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getError().getCode());
            assertEquals("Internal communication failure", e.getError().getReason());
            assertEquals("Internal communication failure", e.getError().getMessage());
        } catch (Exception e) {
            fail("Should not get different exception");
        }
    }

    @Test
    public void should_retrySocketException_when_requestIsIdempotentOrGet() throws Exception {
        HttpRequestRetryHandler retryHandler = sut.getRetryHandler(true, "POST");
        SocketException socketException = new SocketException("Connection failed");
        boolean shouldRetry = retryHandler.retryRequest(socketException, 1, null);
        assertTrue("Should retry idempotent request on socket exception", shouldRetry);

        HttpRequestRetryHandler retryHandler2 = sut.getRetryHandler(false, "GET");
        boolean shouldRetry2 = retryHandler2.retryRequest(socketException, 1, null);
        assertTrue("Should retry GET request on socket exception", shouldRetry2);
    }

    @Test
    public void should_testRetryLogic_with_differentHttpMethods() throws Exception {
        String[] nonGetMethods = {"POST", "PUT", "DELETE", "PATCH"};
        for (String method : nonGetMethods) {
            HttpRequestRetryHandler retryHandler = sut.getRetryHandler(false, method);
            SocketException socketException = new SocketException("Connection failed");
            boolean shouldRetry = retryHandler.retryRequest(socketException, 1, null);
            assertFalse("Should not retry " + method + " request on socket exception when not idempotent", shouldRetry);
        }
    }

    @Test
    public void should_retryBasedOnExceptionType_regardlessofidempotency() throws Exception {
        HttpRequestRetryHandler retryHandler = sut.getRetryHandler(false, "POST");

        Exception[] retryableExceptions = new Exception[] {
            new ConnectionPoolTimeoutException("Pool timeout"),
            new ConnectException("Connection refused"),
            new UnknownHostException("Unknown host")
        };

        for (Exception ex : retryableExceptions) {
            boolean shouldRetry = retryHandler.retryRequest((IOException) ex, 1, null);
            assertTrue(shouldRetry);
        }

        IOException genericException = new IOException("Generic IO error");
        boolean shouldRetry = retryHandler.retryRequest(genericException, 1, null);
        assertFalse("Should not retry generic IOException", shouldRetry);
    }

    @Test
    public void should_notRetry_when_executionCountExceedsRetryLimit() throws Exception {
        HttpRequestRetryHandler retryHandler = sut.getRetryHandler(true, "GET");
        SocketException socketException = new SocketException("Connection failed");
        int executionCountExceedingLimit = HttpClientHandler.RETRY_COUNT + 1;
        boolean shouldRetry = retryHandler.retryRequest(socketException, executionCountExceedingLimit, null);
        assertFalse("Should not retry when execution count exceeds retry limit", shouldRetry);
    }
}
