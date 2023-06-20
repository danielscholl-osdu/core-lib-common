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

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.http.HttpHeaders;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({HttpURLConnection.class, OutputStream.class})
public class HttpClientTest {

    private static final String URL = "http://test.com";
    private static final String BODY = "any http body";
    private static final Map<String, String> HEADERS = new HashMap<>();
    private HttpClient sut;

    @Before
    public void setup() {
        HEADERS.put("header1", "value1");
        this.sut = spy(HttpClient.class);
    }

    @Test
    public void should_throwAppExceptionWithMsg_when_responseThrowsExceptionDuringConnectionCreation() {
        HttpResponse response = this.sut.send(HttpRequest.post().url("invalidURL").headers(HEADERS).body(BODY).build());
        assertTrue(response.hasException());
        assertEquals(IllegalArgumentException.class, response.getException().getClass());
    }


    @Test
    public void should_return403_when_sendingToPublicApiWithoutkey() {
        HttpResponse response = this.sut.send(
                HttpRequest.get().url("https://www.googleapis.com/customsearch/v1?q=hello").headers(HEADERS).build()
        );
        assertEquals(403, response.getResponseCode());
        assertEquals("application/json; charset=UTF-8", response.getContentType());
        assertTrue(response.toString(), response.getLatency() > 0);
        assertTrue(response.getHeaders().size() > 0);
    }

    @Test
    public void should_returnMsgBody_when_makingValidPostRequest() throws Exception {

        HttpRequest rq = HttpRequest.post().body(BODY).url(URL).headers(HEADERS).build();
        createMockHtppConnection(200, rq);
        HttpResponse response = this.sut.send(rq);

        assertEquals(rq, response.getRequest());
        assertEquals("{\"name\":\"test data\"}", response.getBody());
        assertEquals(200, response.getResponseCode());
        assertEquals("application/json", response.getContentType());
    }

    @Test
    public void should_notDoubleEncodingUrl_when_queryComponentsContainsEncodedReservedCharacter() throws Exception {
        String url = "http://test.com?userId=abc%2B123";
        HttpRequest rq = HttpRequest.post().body(BODY).url(url).headers(HEADERS).build();
        createMockHtppConnection(200, rq);
        HttpResponse response = this.sut.send(rq);

        assertEquals(url, response.getRequest().getUrl());
    }

    @Test
    public void should_notEncodeReservedCharacters_when_queryComponentsContainsReservedCharacter() throws Exception {
        String url = "http://test.com?userId=abc+123";
        HttpRequest rq = HttpRequest.post().body(BODY).url(url).headers(HEADERS).build();
        createMockHtppConnection(200, rq);
        HttpResponse response = this.sut.send(rq);

        assertEquals(url, response.getRequest().getUrl());
    }

//      REMOVED RETRY LOGIC FROM HTTPCLIENT FOR NOW AS TOO SLOW - ADD THIS BACK IN WHEN PERFORMANT VERSION FOUND
//    @Test
//    public void should_retryByDefault_when_makingRequestThatReturns501() throws Exception {
//
//        HttpURLConnection connection1 = getHttpURLConnection(501);
//        HttpURLConnection connection2 = getHttpURLConnection(200);
//
//        HttpRequest request = HttpRequest.get().url(URL).headers(HEADERS).build();
//        when(this.sut.createConnection(request)).thenReturn(connection1).thenReturn(connection2);;
//
//        HttpResponse response = this.sut.send(request);
//
//        assertEquals("{\"name\":\"test data\"}", response.getBody());
//        assertEquals(200, response.getResponseCode());
//        assertEquals("application/json", response.getContentType());
//    }


    @Test
    public void should_returnMsgBody_when_makingPostRequestThatIsNon200Response() throws Exception {

        HttpRequest request = HttpRequest.post().body(BODY).url(URL).headers(HEADERS).build();
        createMockHtppConnection(404, request);

        HttpResponse response = this.sut.send(request);

        assertEquals("{\"name\":\"test data\"}", response.getBody());
        assertEquals(404, response.getResponseCode());
        assertEquals("application/json", response.getContentType());
        assertEquals(BODY, response.getRequest().body);
        assertTrue(response.IsNotFoundCode());
    }
    @Test
    public void should_return411_when_makingPostRequestWithoutBodyAndContentLengthNotSetToZero() throws Exception {

        HttpRequest rq = HttpRequest.post().url(URL).headers(HEADERS).build();
        createMockHtppConnection(411, rq);
        HttpResponse response = this.sut.send(rq);

        assertEquals(rq, response.getRequest());
        assertEquals("Content-Length is missing", response.getBody());
        assertEquals(411, response.getResponseCode());
        assertEquals("application/json", response.getContentType());
    }
    @Test
    public void should_return200_when_makingPostRequestWithoutBodyAndContentLengthSetToZero() throws Exception {
        HEADERS.put("Content-Length", Long.toString(0));
        HttpRequest rq = HttpRequest.post().url(URL).headers(HEADERS).build();
        createMockHtppConnection(200, rq);
        HttpResponse response = this.sut.send(rq);

        assertEquals(rq, response.getRequest());
        assertEquals("{\"name\":\"test data\"}", response.getBody());
        assertEquals(200, response.getResponseCode());
        assertEquals("application/json", response.getContentType());
    }
    @Test
    public void should_convertObjectToJson_when_makingPostRequest() throws Exception {

        Integer input = 4;
        HttpRequest request = HttpRequest.post(input).url(URL).headers(HEADERS).build();
        createMockHtppConnection(200, request);

        HttpResponse response = this.sut.send(request);

        assertEquals("4", response.getRequest().body);
    }

    @Test
    public void should_convertObjectToJson_when_makingPutRequest() throws Exception {
        Integer input = 5;
        HttpRequest request = HttpRequest.put(input).url(URL).headers(HEADERS).build();
        createMockHtppConnection(200, request);

        HttpResponse response = this.sut.send(request);

        assertEquals("5", response.getRequest().body);
    }

    @Test
    public void should_convertObjectToJson_when_makingPatchRequest() throws Exception {
        Integer input = 5;
        HttpRequest request = HttpRequest.patch(input).url(URL).headers(HEADERS).build();
        createMockHtppConnection(200, request);

        HttpResponse response = this.sut.send(request);

        assertEquals("5", response.getRequest().body);
    }

    @Test
    public void should_putRequest_when_makingValidPostRequest() throws Exception {

        HttpRequest request = HttpRequest.put().body(BODY).url(URL).headers(HEADERS).build();
        createMockHtppConnection(200, request);

        HttpResponse response = this.sut.send(request);

        assertEquals("{\"name\":\"test data\"}", response.getBody());
        assertEquals(200, response.getResponseCode());
        assertEquals("application/json", response.getContentType());
        assertEquals(BODY, response.getRequest().body);
    }

    @Test
    public void should_returnResponseWithHttp200_when_makingValidGetRequest() throws Exception {
        HttpRequest request = HttpRequest.get().url(URL).headers(HEADERS).build();
        createMockHtppConnection(200, request);
        HttpResponse response = this.sut.send(request);

        assertEquals("{\"name\":\"test data\"}", response.getBody());
        assertEquals(200, response.getResponseCode());
        assertEquals("application/json", response.getContentType());
        assertTrue(response.isSuccessCode());
    }

    @Test
    public void should_returnResponseWith403_when_given403() throws Exception {

        HttpRequest request = HttpRequest.delete().body(BODY).url(URL).headers(HEADERS).build();
        createMockHtppConnection(403, request);
        HttpResponse response = this.sut.send(request);

        assertEquals(403, response.getResponseCode());
        assertTrue(response.IsForbiddenCode());
    }

    @Test
    public void should_returnResponseWith400_when_given400() throws Exception {

        HttpRequest request = HttpRequest.get().body(BODY).url(URL).headers(HEADERS).build();
        createMockHtppConnection(400, request);

        HttpResponse response = this.sut.send(request);

        assertEquals(400, response.getResponseCode());
        assertTrue(response.IsBadRequestCode());
    }

    @Test
    public void should_returnResponseWith401_when_given401() throws Exception {
        HttpRequest request = HttpRequest.get().url(URL).headers(HEADERS).build();
        createMockHtppConnection(401, request);

        HttpResponse response = this.sut.send(request);

        assertEquals(401, response.getResponseCode());
        assertTrue(response.IsUnauthorizedCode());
    }

    private void createMockHtppConnection(int returnCode, HttpRequest request) throws IOException {
        java.net.http.HttpClient client = getHttpClient(returnCode);
        when(this.sut.buildClient(request)).thenReturn(client);
    }

    @SneakyThrows
    private java.net.http.HttpClient getHttpClient(int returnCode) throws IOException {
        java.net.http.HttpClient mock = mock(java.net.http.HttpClient.class);
        java.net.http.HttpResponse httpResponse = mock(java.net.http.HttpResponse.class);
        if(returnCode == 411){
            when(httpResponse.body()).thenReturn("Content-Length is missing");
        }else {
            when(httpResponse.body()).thenReturn("{\"name\":\"test data\"}");
        }
        when(httpResponse.statusCode()).thenReturn(returnCode);
        HttpHeaders httpHeaders = mock(HttpHeaders.class);
        when(httpHeaders.firstValue("content-type")).thenReturn(Optional.of("application/json"));
        when(httpResponse.headers()).thenReturn(httpHeaders);
        when(mock.send(any(), any())).thenReturn(httpResponse);
        return mock;
    }

}