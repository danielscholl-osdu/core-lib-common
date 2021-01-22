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