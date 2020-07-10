package org.opengroup.osdu.core.common.notification;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opengroup.osdu.core.common.http.HttpResponse;
import org.opengroup.osdu.core.common.model.http.DpsException;

@Data
@EqualsAndHashCode(callSuper = false)
public class SubscriptionException extends DpsException {

    private static final long serialVersionUID = -3557182069722613408L;

    public SubscriptionException(String message, HttpResponse httpResponse) {
        super(message, httpResponse);
    }

    public SubscriptionException(String message, HttpResponse httpResponse, Exception ex) {
        super(message, httpResponse);
        this.initCause(ex);
    }
}
