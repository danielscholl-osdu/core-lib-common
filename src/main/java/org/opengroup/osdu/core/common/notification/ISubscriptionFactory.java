package org.opengroup.osdu.core.common.notification;

import org.opengroup.osdu.core.common.model.http.DpsHeaders;

public interface ISubscriptionFactory {

    ISubscriptionService create(DpsHeaders headers);
}