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

package org.opengroup.osdu.core.common.notification;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
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