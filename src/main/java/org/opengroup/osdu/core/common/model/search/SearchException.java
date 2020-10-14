package org.opengroup.osdu.core.common.model.search;

import org.opengroup.osdu.core.common.model.http.DpsException;
import org.opengroup.osdu.core.common.http.HttpResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SearchException extends DpsException {

    private static final long serialVersionUID = 8275957368661334622L;

    public SearchException(String message, HttpResponse httpResponse) {
        super(message, httpResponse);
    }
}