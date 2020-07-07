package org.opengroup.osdu.core.common.notification;

import org.opengroup.osdu.core.common.model.notification.Subscription;
import org.opengroup.osdu.core.common.model.notification.SubscriptionInfo;
import org.opengroup.osdu.core.common.model.notification.Topic;

import java.util.List;

public interface ISubscriptionService {

    Subscription create(Subscription lt) throws SubscriptionException;

    SubscriptionInfo get(String subscriptionId) throws SubscriptionException;

    void delete(String subscriptionId) throws SubscriptionException;

    List<Topic> getTopics() throws SubscriptionException;

    List<Subscription> query(String notificationId) throws SubscriptionException;
}