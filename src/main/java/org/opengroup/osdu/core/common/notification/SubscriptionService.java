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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opengroup.osdu.core.common.http.HttpRequest;
import org.opengroup.osdu.core.common.http.HttpResponse;
import org.opengroup.osdu.core.common.http.IHttpClient;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.model.notification.Subscription;
import org.opengroup.osdu.core.common.model.notification.SubscriptionInfo;
import org.opengroup.osdu.core.common.model.notification.Topic;

import java.io.IOException;
import java.util.List;
import org.opengroup.osdu.core.common.util.UrlNormalizationUtil;

public class SubscriptionService implements ISubscriptionService {
    public SubscriptionService(SubscriptionAPIConfig config,
                        IHttpClient httpClient,
                        DpsHeaders headers) {
        this.rootUrl = config.getRootUrl();
        this.httpClient = httpClient;
        this.headers = headers;
        headers.put("AppKey", config.apiKey);
    }

    private final String rootUrl;
    private final IHttpClient httpClient;
    private final DpsHeaders headers;


    @Override
    public Subscription create(Subscription lt) throws SubscriptionException {
        String url = this.createUrl("/subscription");
        HttpResponse result = this.httpClient.send(
                HttpRequest.post(lt).url(url).headers(this.headers.getHeaders()).build());
        return getSubscription(result);
    }

    @Override
    public SubscriptionInfo get(String subscriptionId) throws SubscriptionException {
        String url = this.createUrl(String.format("/subscription/%s", subscriptionId));
        HttpResponse result = this.httpClient.send(
                HttpRequest.get().url(url).headers(this.headers.getHeaders()).build());
        return result.IsNotFoundCode() ? null : new SubscriptionInfo(getSubscription(result));
    }

    @Override
    public void delete(String subscriptionId) throws SubscriptionException {
        String url = this.createUrl(String.format("/subscription/%s", subscriptionId));
        HttpResponse result = this.httpClient.send(
                HttpRequest.delete().url(url).headers(this.headers.getHeaders()).build());

        if(!result.isSuccessCode())
            throw new SubscriptionException("Error making request to Register service. Check the inner HttpResponse for more info.", result); }

    @Override
    public List<Topic> getTopics() throws SubscriptionException {
        String url = this.createUrl(String.format("/topics"));
        HttpResponse result = this.httpClient.send(
                HttpRequest.get().url(url).headers(this.headers.getHeaders()).build());
        return getTopics(result);
    }

    @Override
    public List<Subscription> query(String notificationId) throws SubscriptionException {
        String url = this.createUrl(String.format("/subscription?notificationId=%s", notificationId));
        HttpResponse result = this.httpClient.send(
                HttpRequest.get().url(url).headers(this.headers.getHeaders()).build());
        return getListOfSubscriptions(result);
    }

    private String createUrl(String pathAndQuery) {
        return UrlNormalizationUtil.normalizeStringUrl(this.rootUrl,pathAndQuery);
    }

    static Subscription getSubscription(HttpResponse result) throws SubscriptionException {
        if (result.isSuccessCode()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(result.getBody(), Subscription.class);
            } catch (IOException  ex) {
                throw new SubscriptionException("Exception in deserializing reponse", result, ex);
            }
        } else {
            throw new SubscriptionException("Error making request to Register service. Check the inner HttpResponse for more info.", result); }
    }

    static List<Topic> getTopics(HttpResponse result) throws SubscriptionException {
        if (result.isSuccessCode()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(result.getBody(), new TypeReference<List<Topic>>(){});
            } catch (IOException  ex) {
                throw new SubscriptionException("Exception in deserializing reponse", result, ex);
            }
        } else {
            throw new SubscriptionException("Error making request to Register service. Check the inner HttpResponse for more info.", result); }
    }

    private SubscriptionException generateException(HttpResponse result) {
        return new SubscriptionException("Error making request to Register service. Check the inner HttpResponse for more info.", result); }

    private List<Subscription> getListOfSubscriptions(HttpResponse result) throws SubscriptionException {
        if (result.isSuccessCode()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(result.getBody(), new TypeReference<List<Subscription>>(){});
            } catch (IOException  ex) {
                throw new SubscriptionException("Exception in deserializing reponse", result, ex);
            }
        } else {
            throw new SubscriptionException("Error making request to Register service. Check the inner HttpResponse for more info.", result);
        }
    }
}
