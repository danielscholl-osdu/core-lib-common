package org.opengroup.osdu.core.common.entitlements;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.opengroup.osdu.core.common.model.entitlements.EntitlementsException;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;

@RunWith(MockitoJUnitRunner.class)
public class EntitlementsServiceTest {

	public static final String ROOT_URL = "http://example.com";

	@Mock
	private IHttpClient httpClient;
	@Mock
	private DpsHeaders headers;
	@Mock
	private HttpResponseBodyMapper responseBodyMapper;
	@Mock
	private EntitlementsAPIConfig config;

	private EntitlementsService entitlementsService;

	@Test
	public void testUrlNormalization() throws EntitlementsException{
		HttpResponse response = new HttpResponse();
		response.setResponseCode(200);
		when(httpClient.send(any())).thenReturn(response);
		String malformedRootUrl = " \n  " + ROOT_URL + "\n // \t \f \r";
		Mockito.when(config.getRootUrl()).thenReturn(malformedRootUrl);
		entitlementsService = new EntitlementsService(config, httpClient, headers, responseBodyMapper);

		entitlementsService.getGroups();
		ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
		verify(httpClient).send(captor.capture());
		assertEquals(ROOT_URL + "/groups", captor.getValue().getUrl());
	}
}