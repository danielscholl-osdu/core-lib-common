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

package org.opengroup.osdu.core.common.crs;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opengroup.osdu.core.common.http.HttpResponse;
import org.opengroup.osdu.core.common.http.json.HttpResponseBodyMapper;
import org.opengroup.osdu.core.common.http.json.HttpResponseBodyParsingException;
import org.opengroup.osdu.core.common.model.crs.ConvertGeoJsonRequest;
import org.opengroup.osdu.core.common.model.crs.ConvertPointsRequest;
import org.opengroup.osdu.core.common.model.crs.ConvertPointsResponse;
import org.opengroup.osdu.core.common.model.crs.CrsConverterException;
import org.opengroup.osdu.core.common.model.crs.GeoJson.GeoJsonFeatureCollection;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CrsConverterServiceTest {
    private static final int STATUS = 200;
    private static final String VALID_JSON_RESPONSE = "valid Json Response";
    private static final String FROM_CRS = "fromCRS";
    private static final String TO_CRS = "toCRS";
    private static final String TO_UNIT_Z = "toUnitZ";
    private static final String ROOT_URL = "http://example.com";
    private static final Map<String, String> HEADERS_MAP = ImmutableMap.of("header1", "value1", "header2", "value2");

    @Mock
    private CloseableHttpClient httpClient;
    @Mock
    private DpsHeaders headers;
    @Mock
    private HttpResponseBodyMapper responseBodyMapper;
    @Mock
    private CrsConverterAPIConfig crsConverterAPIConfig;
    @Mock
    private CloseableHttpResponse mockResponse;
    @Captor
    private ArgumentCaptor<HttpResponse> responseCaptor;
    private CrsConverterService sut;
    private final Gson gson = new Gson();

    @Before
    public void init() throws IOException {
        mockResponse = getResponse(STATUS, VALID_JSON_RESPONSE);
        when(httpClient.execute(any())).thenReturn(mockResponse);
        String malformedRootUrl = " \n  " + ROOT_URL + "\n // \t \f \r";
        when(crsConverterAPIConfig.getRootUrl()).thenReturn(malformedRootUrl);
        when(headers.getHeaders()).thenReturn(HEADERS_MAP);
        sut = new CrsConverterService(crsConverterAPIConfig, httpClient, headers, responseBodyMapper);
    }

    @Test
    public void testUrlNormalization() throws CrsConverterException, IOException {
        sut.convertPoints(new ConvertPointsRequest(FROM_CRS, TO_CRS, Collections.emptyList()));
        ArgumentCaptor<HttpPost> captor = ArgumentCaptor.forClass(HttpPost.class);
        verify(httpClient).execute(captor.capture());
        assertEquals(ROOT_URL + "/convert", captor.getValue().getURI().toString());
    }

    @Test
    public void testUrlNormalizationForGeoJson() throws CrsConverterException, IOException {
        GeoJsonFeatureCollection fc = null;
        sut.convertGeoJson(new ConvertGeoJsonRequest(fc, TO_CRS, TO_UNIT_Z));
        ArgumentCaptor<HttpPost> captor = ArgumentCaptor.forClass(HttpPost.class);
        verify(httpClient).execute(captor.capture());
        assertEquals(ROOT_URL + "/convertGeoJson", captor.getValue().getURI().toString());
    }

    @Test
    public void shouldConvertPoints() throws CrsConverterException, IOException, HttpResponseBodyParsingException {
        ConvertPointsRequest request = new ConvertPointsRequest(FROM_CRS, TO_CRS, Collections.emptyList());
        ConvertPointsResponse response = new ConvertPointsResponse();
        when(responseBodyMapper.parseBody(responseCaptor.capture(), any())).thenReturn(response);

        ConvertPointsResponse result = sut.convertPoints(request);

        ArgumentCaptor<HttpPost> httpPostCaptor = ArgumentCaptor.forClass(HttpPost.class);
        verify(httpClient).execute(httpPostCaptor.capture());
        HttpPost httpPostValue = httpPostCaptor.getValue();
        assertEquals(ROOT_URL + "/convert", httpPostValue.getURI().toString());
        assertEquals("value1", httpPostValue.getFirstHeader("header1").getElements()[0].getName());
        assertEquals("value2", httpPostValue.getFirstHeader("header2").getElements()[0].getName());
        assertEquals(new StringEntity(gson.toJson(request)).toString(), httpPostValue.getEntity().toString());

        ArgumentCaptor<HttpResponse> responseCaptor = ArgumentCaptor.forClass(HttpResponse.class);
        verify(responseBodyMapper).parseBody(responseCaptor.capture(), any());
        HttpResponse httpResponseValue = responseCaptor.getValue();
        assertEquals(STATUS, httpResponseValue.getResponseCode());
        assertEquals(VALID_JSON_RESPONSE, httpResponseValue.getBody());

        assertNotNull(result);
    }

    private CloseableHttpResponse getResponse(int status, String body) throws IOException {
        HttpEntity entity = mock(HttpEntity.class);
        when(mockResponse.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, status, "OK"));
        if (body != null) {
            when(entity.getContent()).thenReturn(new ByteArrayInputStream(body.getBytes()));
        }
        when(mockResponse.getEntity()).thenReturn(entity);
        return mockResponse;
    }
}