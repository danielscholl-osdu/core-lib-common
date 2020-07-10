package org.opengroup.osdu.core.common.notification;

import org.opengroup.osdu.core.common.http.HttpClient;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;

public class SubscriptionFactory implements ISubscriptionFactory {

    public SubscriptionFactory(SubscriptionAPIConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("SubscriptionAPIConfig cannot be empty");
        }
        this.config = config;
    }

    private final SubscriptionAPIConfig config;

    @Override
    public ISubscriptionService create(DpsHeaders headers) {
        if (headers == null) {
            throw new NullPointerException("headers cannot be null");
        }
        return new SubscriptionService(this.config, new HttpClient(), headers);
    }
}
