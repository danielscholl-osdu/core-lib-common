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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.opengroup.osdu.core.common.http.HttpRequest;
import org.opengroup.osdu.core.common.http.HttpResponse;
import org.opengroup.osdu.core.common.http.IHttpClient;
import org.opengroup.osdu.core.common.http.json.HttpResponseBodyMapper;
import org.opengroup.osdu.core.common.model.crs.ConvertGeoJsonRequest;
import org.opengroup.osdu.core.common.model.crs.ConvertPointsRequest;
import org.opengroup.osdu.core.common.model.crs.CrsConverterException;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.model.crs.GeoJson.GeoJsonFeatureCollection;

@RunWith(MockitoJUnitRunner.class)
public class CrsConverterServiceTest {

	public static final String ROOT_URL = "http://example.com";
	@Mock
	private IHttpClient httpClient;
	@Mock
	private DpsHeaders headers;
	@Mock
	private HttpResponseBodyMapper responseBodyMapper;
	@Mock
	private CrsConverterAPIConfig crsConverterAPIConfig;

	private CrsConverterService crsConverterService;

	@Test
	public void testUrlNormalization() throws CrsConverterException {
		HttpResponse response = new HttpResponse();
		response.setResponseCode(200);
		when(httpClient.send(any())).thenReturn(response);
		String malformedRootUrl = " \n  " + ROOT_URL + "\n // \t \f \r";
		Mockito.when(crsConverterAPIConfig.getRootUrl()).thenReturn(malformedRootUrl);
		crsConverterService = new CrsConverterService(crsConverterAPIConfig, httpClient, headers, responseBodyMapper);

		crsConverterService.convertPoints(new ConvertPointsRequest("","", Collections.EMPTY_LIST));
		ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
		verify(httpClient).send(captor.capture());
		assertEquals(ROOT_URL + "/convert", captor.getValue().getUrl());
	}

	@Test
	public void testUrlNormalizationForGeoJson() throws CrsConverterException {
		HttpResponse response = new HttpResponse();
		response.setResponseCode(200);
		when(httpClient.send(any())).thenReturn(response);
		String malformedRootUrl = " \n  " + ROOT_URL + "\n // \t \f \r";
		Mockito.when(crsConverterAPIConfig.getRootUrl()).thenReturn(malformedRootUrl);
		crsConverterService = new CrsConverterService(crsConverterAPIConfig, httpClient, headers, responseBodyMapper);
		GeoJsonFeatureCollection fc = null;

		crsConverterService.convertGeoJson(new ConvertGeoJsonRequest(fc,"", ""));
		ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
		verify(httpClient).send(captor.capture());
		assertEquals(ROOT_URL + "/convertGeoJson", captor.getValue().getUrl());
	}
}