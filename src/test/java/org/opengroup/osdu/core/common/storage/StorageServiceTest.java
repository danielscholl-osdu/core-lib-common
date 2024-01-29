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

package org.opengroup.osdu.core.common.storage;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.opengroup.osdu.core.common.http.HttpClient;
import org.opengroup.osdu.core.common.http.HttpRequest;
import org.opengroup.osdu.core.common.http.HttpResponse;
import org.opengroup.osdu.core.common.http.IHttpClient;
import org.opengroup.osdu.core.common.http.json.HttpResponseBodyMapper;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.model.storage.Record;
import org.opengroup.osdu.core.common.model.storage.StorageException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class StorageServiceTest {

    private static final String ROOT_URL = "http://example.com";

    private IHttpClient httpClient;

    private StorageAPIConfig config;

    private DpsHeaders dpsHeaders;

    private HttpResponseBodyMapper bodyMapper;

    @Before
    public void setup() {
        config = new StorageAPIConfig("url", "any key");
        dpsHeaders = new DpsHeaders();
        httpClient = mock(HttpClient.class);
        bodyMapper = mock(HttpResponseBodyMapper.class);
    }

    @Test
    public void constructorTest() {
        StorageService storageService = new StorageService(config, httpClient, dpsHeaders, bodyMapper);
        assertNotNull(storageService);
    }

    @Test(expected = StorageException.class)
    public void should_throw_Exception_when_response_is_empty() throws StorageException {
        HttpResponse httpResponse = new HttpResponse();
        StorageService storageService = new StorageService(config, httpClient, dpsHeaders, bodyMapper);
        when(httpClient.send(any(HttpRequest.class))).thenReturn(httpResponse);
        storageService.getRecord("AnyRecord");
    }

    @Test
    public void should_return_null_when_response_is_invalid() throws StorageException {
        HttpResponse httpResponse = mock(HttpResponse.class);
        StorageService storageService = new StorageService(config, httpClient, dpsHeaders, bodyMapper);
        when(httpResponse.isSuccessCode()).thenReturn(true);
        when(httpClient.send(any(HttpRequest.class))).thenReturn(httpResponse);
        Record record = storageService.getRecord("AnyRecord");
        assertNull(record);
    }

    @Test
    public void should_return_valid_response_when_response_is_valid() throws Exception {
        HttpResponse httpResponse = mock(HttpResponse.class);
        StorageService storageService = new StorageService(config, httpClient, dpsHeaders, bodyMapper);
        when(httpResponse.isSuccessCode()).thenReturn(true);
        when(bodyMapper.parseBody(httpResponse, Record.class)).thenReturn(new Record());
        when(httpClient.send(any(HttpRequest.class))).thenReturn(httpResponse);
        Record record = storageService.getRecord("AnyRecord");
        assertNotNull(record);
    }

    @Test
    public void testUrlNormalization () throws StorageException {
        HttpResponse httpResponse = mock(HttpResponse.class);
        when(httpResponse.isSuccessCode()).thenReturn(true);
        when(httpClient.send(any(HttpRequest.class))).thenReturn(httpResponse);
        String malformedUrl = " \n  " + ROOT_URL + "\n // \t \f \r";
        config = StorageAPIConfig.builder().rootUrl(malformedUrl).build();
        StorageService storageService = new StorageService(config, httpClient, dpsHeaders, bodyMapper);

        storageService.getRecord("any");
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
        Mockito.verify(httpClient).send(captor.capture());
        assertEquals(ROOT_URL + "/records/any",captor.getValue().getUrl());
    }
}