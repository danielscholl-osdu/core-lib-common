package org.opengroup.osdu.core.common.cryptographic;

public interface ISignatureService {

    String getSignedSignature(String url, String secret) throws SignatureServiceException;

    String getSignedSignature(String url, String secret, String expireTime, String nonce) throws SignatureServiceException;

    void verifyHmacSignature(String hmac, String secret) throws SignatureServiceException;
}