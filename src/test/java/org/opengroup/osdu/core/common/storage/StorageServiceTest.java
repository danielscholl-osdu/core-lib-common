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
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class StorageServiceTest {

    private static final String ROOT_URL = "http://example.com";

    private IHttpClient httpClient;

    private StorageAPIConfig config;

    private DpsHeaders dpsHeaders;

    private HttpResponseBodyMapper bodyMapper;

    @Before
    public void setup() {
        config = new StorageAPIConfig(ROOT_URL, "any key");
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
    public void softDeleteRecord_calls_correct_endpoint() throws Exception {
        HttpResponse httpResponse = mock(HttpResponse.class);
        StorageService storageService = new StorageService(config, httpClient, dpsHeaders, bodyMapper);
        when(httpResponse.isSuccessCode()).thenReturn(true);
        when(bodyMapper.parseBody(httpResponse, String.class)).thenReturn("");
        when(httpClient.send(any(HttpRequest.class))).thenReturn(httpResponse);

        String recordId = "1234";
        storageService.softDeleteRecord(recordId);

        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(captor.capture());
        assertEquals(String.format("%s/records/%s:delete", ROOT_URL, recordId), captor.getValue().getUrl());
        assertEquals(HttpRequest.POST, captor.getValue().getHttpMethod());
    }

    @Test
    public void softDeleteRecords_calls_correct_endpoint() throws Exception {
        HttpResponse httpResponse = mock(HttpResponse.class);
        StorageService storageService = new StorageService(config, httpClient, dpsHeaders, bodyMapper);
        when(httpResponse.isSuccessCode()).thenReturn(true);
        when(bodyMapper.parseBody(httpResponse, String.class)).thenReturn("");
        when(httpClient.send(any(HttpRequest.class))).thenReturn(httpResponse);

        String recordId = "1234";
        Collection<String> ids = Arrays.asList(recordId);
        storageService.softDeleteRecords(ids);

        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(captor.capture());
        assertEquals(String.format("%s/records/delete", ROOT_URL), captor.getValue().getUrl());
        assertEquals(HttpRequest.POST, captor.getValue().getHttpMethod());
        assertTrue(captor.getValue().getBody().contains(recordId));
    }

    @Test
    public void purgeRecordVersions_calls_correct_endpoint_with_ids() throws Exception {
        HttpResponse httpResponse = mock(HttpResponse.class);
        StorageService storageService = new StorageService(config, httpClient, dpsHeaders, bodyMapper);
        when(httpResponse.isSuccessCode()).thenReturn(true);
        when(bodyMapper.parseBody(httpResponse, String.class)).thenReturn("");
        when(httpClient.send(any(HttpRequest.class))).thenReturn(httpResponse);

        String recordId = "1234";
        String recordVersion1 = "v1";
        String recordVersion2 = "v2";
        Collection<String> versionIds = Arrays.asList(recordVersion1, recordVersion2);
        storageService.purgeRecordVersions(recordId, versionIds);

        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(captor.capture());
        assertEquals(String.format("%s/records/%s/versions", ROOT_URL, recordId), captor.getValue().getUrl());
        assertEquals(HttpRequest.DELETE, captor.getValue().getHttpMethod());
        Map<String, String> queryParams = captor.getValue().getQueryParams();
        assertEquals(1, queryParams.size());
        assertEquals(String.format("%s,%s", recordVersion1, recordVersion2), queryParams.get("versionIds"));
    }

    @Test
    public void purgeRecordVersions_calls_correct_endpoint_with_limit() throws Exception {
        HttpResponse httpResponse = mock(HttpResponse.class);
        StorageService storageService = new StorageService(config, httpClient, dpsHeaders, bodyMapper);
        when(httpResponse.isSuccessCode()).thenReturn(true);
        when(bodyMapper.parseBody(httpResponse, String.class)).thenReturn("");
        when(httpClient.send(any(HttpRequest.class))).thenReturn(httpResponse);

        String recordId = "1234";
        Integer limit = 42;
        Long fromVersion = null;
        storageService.purgeRecordVersions(recordId, limit, fromVersion);

        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(captor.capture());
        assertEquals(String.format("%s/records/%s/versions", ROOT_URL, recordId), captor.getValue().getUrl());
        assertEquals(HttpRequest.DELETE, captor.getValue().getHttpMethod());
        Map<String, String> queryParams = captor.getValue().getQueryParams();
        assertEquals(1, queryParams.size());
        assertEquals(Integer.toString(limit), queryParams.get("limit"));
    }

    @Test
    public void purgeRecordVersions_calls_correct_endpoint_with_fromVersion() throws Exception {
        HttpResponse httpResponse = mock(HttpResponse.class);
        StorageService storageService = new StorageService(config, httpClient, dpsHeaders, bodyMapper);
        when(httpResponse.isSuccessCode()).thenReturn(true);
        when(bodyMapper.parseBody(httpResponse, String.class)).thenReturn("");
        when(httpClient.send(any(HttpRequest.class))).thenReturn(httpResponse);

        String recordId = "1234";
        Integer limit = null;
        Long fromVersion = 4242l;
        storageService.purgeRecordVersions(recordId, limit, fromVersion);

        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(captor.capture());
        assertEquals(String.format("%s/records/%s/versions", ROOT_URL, recordId), captor.getValue().getUrl());
        assertEquals(HttpRequest.DELETE, captor.getValue().getHttpMethod());
        Map<String, String> queryParams = captor.getValue().getQueryParams();
        assertEquals(1, queryParams.size());
        assertEquals(Long.toString(fromVersion), queryParams.get("fromVersion"));
    }

    @Test
    public void purgeRecordVersions_calls_correct_endpoint_with_limit_and_fromVersion() throws Exception {
        HttpResponse httpResponse = mock(HttpResponse.class);
        StorageService storageService = new StorageService(config, httpClient, dpsHeaders, bodyMapper);
        when(httpResponse.isSuccessCode()).thenReturn(true);
        when(bodyMapper.parseBody(httpResponse, String.class)).thenReturn("");
        when(httpClient.send(any(HttpRequest.class))).thenReturn(httpResponse);

        String recordId = "1234";
        Integer limit = 42;
        Long fromVersion = 4242l;
        storageService.purgeRecordVersions(recordId, limit, fromVersion);

        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(captor.capture());
        assertEquals(String.format("%s/records/%s/versions", ROOT_URL, recordId), captor.getValue().getUrl());
        assertEquals(HttpRequest.DELETE, captor.getValue().getHttpMethod());
        Map<String, String> queryParams = captor.getValue().getQueryParams();
        assertEquals(2, queryParams.size());
        assertEquals(Integer.toString(limit), queryParams.get("limit"));
        assertEquals(Long.toString(fromVersion), queryParams.get("fromVersion"));
    }

    @Test
    public void testUrlNormalization () throws StorageException {
        HttpResponse httpResponse = mock(HttpResponse.class);
        when(httpResponse.isSuccessCode()).thenReturn(true);
        when(httpClient.send(any(HttpRequest.class))).thenReturn(httpResponse);
        String malformedUrl = " \n  " + ROOT_URL + "\n // \t \f \r";
        config = StorageAPIConfig.builder().rootUrl(malformedUrl).build();
        StorageService storageService = new StorageService(config, httpClient, dpsHeaders, bodyMapper);

        String recordId = "any";
        storageService.getRecord(recordId);
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(captor.capture());
        assertEquals(String.format("%s/records/%s", ROOT_URL, recordId), captor.getValue().getUrl());
    }
}