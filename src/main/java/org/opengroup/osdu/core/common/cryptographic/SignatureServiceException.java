package org.opengroup.osdu.core.common.cryptographic;

public class SignatureServiceException extends Exception {
    private static final long serialVersionUID = -4393652925816393733L;

    SignatureServiceException(String errorMessage) {
        super(errorMessage);
    }

    SignatureServiceException(String errorMessage, Exception e) {
        super(errorMessage);
        this.initCause(e);
    }
}
