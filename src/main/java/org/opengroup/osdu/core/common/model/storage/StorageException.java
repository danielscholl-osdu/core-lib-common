package org.opengroup.osdu.core.common.model.storage;

import org.opengroup.osdu.core.common.model.http.DpsException;
import org.opengroup.osdu.core.common.http.HttpResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class StorageException extends DpsException {

    private static final long serialVersionUID = -3823738766134121467L;

    public StorageException(String message, HttpResponse httpResponse) {
        super(message, httpResponse);
    }
}