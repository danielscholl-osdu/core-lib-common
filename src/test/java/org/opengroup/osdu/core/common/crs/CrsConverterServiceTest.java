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

import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opengroup.osdu.core.common.http.json.HttpResponseBodyMapper;
import org.opengroup.osdu.core.common.model.crs.ConvertGeoJsonRequest;
import org.opengroup.osdu.core.common.model.crs.ConvertPointsRequest;
import org.opengroup.osdu.core.common.model.crs.CrsConverterException;
import org.opengroup.osdu.core.common.model.crs.GeoJson.GeoJsonFeatureCollection;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CrsConverterServiceTest {
	private static final String VALID_JSON_RESPONSE = "valid Json Response";
	public static final String ROOT_URL = "http://example.com";

	@Mock
	private CloseableHttpClient httpClient;
	@Mock
	private DpsHeaders headers;
	@Mock
	private HttpResponseBodyMapper responseBodyMapper;
	@Mock
	private CrsConverterAPIConfig crsConverterAPIConfig;

	private CloseableHttpResponse mockResponse;
	private CrsConverterService sut;

	@Before
	public void init() throws IOException {
		mockResponse = getResponse(200, VALID_JSON_RESPONSE);
		when(httpClient.execute(any())).thenReturn(mockResponse);
		String malformedRootUrl = " \n  " + ROOT_URL + "\n // \t \f \r";
		when(crsConverterAPIConfig.getRootUrl()).thenReturn(malformedRootUrl);
		sut = new CrsConverterService(crsConverterAPIConfig, httpClient, headers, responseBodyMapper);
	}

	@Test
	public void testUrlNormalization() throws CrsConverterException, IOException {
		sut.convertPoints(new ConvertPointsRequest("","", Collections.EMPTY_LIST));
		ArgumentCaptor<HttpPost> captor = ArgumentCaptor.forClass(HttpPost.class);
		verify(httpClient).execute(captor.capture());
		assertEquals(ROOT_URL + "/convert", captor.getValue().getURI().toString());
	}

	@Test
	public void testUrlNormalizationForGeoJson() throws CrsConverterException, IOException {
		GeoJsonFeatureCollection fc = null;
		sut.convertGeoJson(new ConvertGeoJsonRequest(fc,"", ""));
		ArgumentCaptor<HttpPost> captor = ArgumentCaptor.forClass(HttpPost.class);
		verify(httpClient).execute(captor.capture());
		assertEquals(ROOT_URL + "/convertGeoJson", captor.getValue().getURI().toString());
	}

	private CloseableHttpResponse getResponse(int status, String body) throws IOException {
		CloseableHttpResponse response = mock(CloseableHttpResponse.class);
		HttpEntity entity = mock(HttpEntity.class);
		when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, status, "OK"));
		if (body != null) {
			when(entity.getContent()).thenReturn(new ByteArrayInputStream(body.getBytes()));
		}
		when(response.getEntity()).thenReturn(entity);
		return response;
	}
}