package org.opengroup.osdu.core.common.cryptographic;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
@RequestScope
public class SignatureService implements ISignatureService {

    private static final String HMAC_SHA_256 = "HmacSHA256";
    private static final String DATA_FORMAT = "{\"expireMillisecond\": \"%s\",\"hashMechanism\": \"hmacSHA256\",\"endpointUrl\": \"%s\",\"nonce\": \"%s\"}";
    private static final String NOTIFICATION_SERVICE = "de-notification-service";
    private static final long EXPIRE_DURATION = 30000L;

    private static final String INVALID_SIGNATURE = "Invalid signature";
    private static final String ERROR_GENERATING_SIGNATURE = "Error generating the signature";
    private static final String SIGNATURE_EXPIRED = "Signature is expired";
    private static final String MISSING_HMAC_SIGNATURE = "HMAC signature should not be null or empty";
    private static final String MISSING_SECRET_VALUE = "Secret should not be null or empty";
    private static final String MISSING_ATTRIBUTES_IN_SIGNATURE = "Missing url or nonce or expire time in the signature";


    @Override
    public String getSignedSignature(String url, String secret) throws SignatureServiceException {
        if (Strings.isNullOrEmpty(url) || Strings.isNullOrEmpty(secret)) {
            throw new SignatureServiceException(ERROR_GENERATING_SIGNATURE);
        }
        final long currentTime = System.currentTimeMillis();
        final String expireTime = String.valueOf(currentTime + EXPIRE_DURATION);
        final String timeStamp = String.valueOf(currentTime);
        try {
            String nonce = DatatypeConverter.printHexBinary(generateRandomBytes(16)).toLowerCase();
            String data = String.format(DATA_FORMAT, expireTime, url, nonce);
            final byte[] signature = getSignature(secret, nonce, timeStamp, data);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            String dataBytesEncoded = Base64.getEncoder().encodeToString(dataBytes);
            StringBuilder output = new StringBuilder();
            output.append(dataBytesEncoded)
                    .append(".")
                    .append(DatatypeConverter.printHexBinary(signature).toLowerCase());

            return output.toString();
        } catch (Exception ex) {
            throw new SignatureServiceException(ERROR_GENERATING_SIGNATURE, ex);
        }
    }

    @Override
    public String getSignedSignature(String url, String secret, String expireTime, String nonce) throws SignatureServiceException {
        if (Strings.isNullOrEmpty(url) || Strings.isNullOrEmpty(secret) || !StringUtils.isNumeric(expireTime)) {
            throw new SignatureServiceException(ERROR_GENERATING_SIGNATURE);
        }
        final long expiry = Long.parseLong(expireTime);
        if (System.currentTimeMillis() > expiry) {
            throw new SignatureServiceException(SIGNATURE_EXPIRED);
        }
        String timeStamp = String.valueOf(expiry - EXPIRE_DURATION);
        String data = String.format(DATA_FORMAT, expireTime, url, nonce);
        try {
            final byte[] signature = getSignature(secret, nonce, timeStamp, data);
            return DatatypeConverter.printHexBinary(signature).toLowerCase();
        } catch (Exception ex) {
            throw new SignatureServiceException(ERROR_GENERATING_SIGNATURE, ex);
        }
    }


    @Override
    public void verifyHmacSignature(String hmac, String secret) throws SignatureServiceException {
        if (Strings.isNullOrEmpty(hmac)) {
            throw new SignatureServiceException(MISSING_HMAC_SIGNATURE);
        }
        if (Strings.isNullOrEmpty(secret)) {
            throw new SignatureServiceException(MISSING_SECRET_VALUE);
        }
        String[] tokens = hmac.split("\\.");
        if (tokens.length != 2) {
            throw new SignatureServiceException(INVALID_SIGNATURE);
        }
        byte[] dataBytes = Base64.getDecoder().decode(tokens[0]);
        String requestSignature = tokens[1];

        String data = new String(dataBytes, StandardCharsets.UTF_8);
        HmacData hmacData = new Gson().fromJson(data, HmacData.class);
        String url = hmacData.getEndpointUrl();
        String nonce = hmacData.getNonce();
        String expireTime = hmacData.getExpireMillisecond();
        if (Strings.isNullOrEmpty(url) || Strings.isNullOrEmpty(nonce) || Strings.isNullOrEmpty(expireTime)) {
            throw new SignatureServiceException(MISSING_ATTRIBUTES_IN_SIGNATURE);
        }
        String newSignature = getSignedSignature(url, secret, expireTime, nonce);
        if (!requestSignature.equalsIgnoreCase(newSignature)) {
            throw new SignatureServiceException(INVALID_SIGNATURE);
        }
    }

    private byte[] getSignature(String secret, String nonce, String timeStamp, String data) throws Exception {
        final byte[] secretBytes = DatatypeConverter.parseHexBinary(secret);
        final byte[] nonceBytes = DatatypeConverter.parseHexBinary(nonce);
        final byte[] encryptedNonce = computeHmacSha256(nonceBytes, secretBytes);
        final byte[] encryptedTimestamp = computeHmacSha256(timeStamp, encryptedNonce);
        final byte[] signedKey = computeHmacSha256(NOTIFICATION_SERVICE, encryptedTimestamp);
        final byte[] signature = computeHmacSha256(data, signedKey);
        return signature;
    }

    private byte[] computeHmacSha256(final String data, final byte[] key) throws Exception {
        final Mac mac = Mac.getInstance(HMAC_SHA_256);
        mac.init(new SecretKeySpec(key, HMAC_SHA_256));
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    private byte[] computeHmacSha256(final byte[] data, final byte[] key) throws Exception {
        final Mac mac = Mac.getInstance(HMAC_SHA_256);
        mac.init(new SecretKeySpec(key, HMAC_SHA_256));
        return mac.doFinal(data);
    }

    private byte[] generateRandomBytes(final int size) {
        final byte[] key = new byte[size];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(key);
        return key;
    }
}
