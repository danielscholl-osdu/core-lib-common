package org.opengroup.osdu.core.common.notification;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.opengroup.osdu.core.common.http.HttpRequest;
import org.opengroup.osdu.core.common.http.HttpResponse;
import org.opengroup.osdu.core.common.http.IHttpClient;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.model.notification.Subscription;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionServiceTest {

	public static final String ROOT_URL = "http://example.com";

	@Mock
	private IHttpClient httpClient;

	@Mock
	private DpsHeaders headers;

	@Mock
	private SubscriptionAPIConfig subscriptionAPIConfig;

	private SubscriptionService subscriptionService;

	private Subscription subscription;

	private ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void testUrlNormalization() throws SubscriptionException, JsonProcessingException {
		String malformedRootUrl = " \n  " + ROOT_URL + "\n // \t \f \r";
		Mockito.when(subscriptionAPIConfig.getRootUrl()).thenReturn(malformedRootUrl);
		HttpResponse response = new HttpResponse();
		response.setResponseCode(200);
		subscription = new Subscription();
		response.setBody(objectMapper.writeValueAsString(subscription));
		when(httpClient.send(any())).thenReturn(response);
		subscriptionService = new SubscriptionService(subscriptionAPIConfig, httpClient, headers);

		subscriptionService.get("any");
		ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
		verify(httpClient).send(captor.capture());
		assertEquals(ROOT_URL + "/subscription/any", captor.getValue().getUrl());
	}
}