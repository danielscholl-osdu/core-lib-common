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

package org.opengroup.osdu.core.common.cryptographic;

import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import java.util.Base64;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*", "javax.crypto.*"})
public class SignatureServiceTest {

    @InjectMocks
    private SignatureService sut;

    private static final String HMAC_INVALID = "invalid";
    private static final String SECRET = "123abc";
    private static final String ANOTHER_SECRET = "123def";
    private static final String ENDPOINT_URL = "http://unittest:123/";
    private static final String HMAC_VALID_EXPIRED = "eyJleHBpcmVNaWxsaXNlY29uZCI6ICIxNTc2Njk5MDU3NjcxIiwiaGFzaE1lY2hhbmlzbSI6ICJobWFjU0hBMjU2IiwiZW5kcG9pbnRVcmwiOiAiaHR0cDovL2xvY2FsaG9zdDo4MDgwL19haC9wdXNoLWhhbmRsZXJzL3Rlc3QiLCJub25jZSI6ICI0OGJlYjRiNTc3NmMzZDkwMzNhNDlhMWJkZTRhYTU0YyJ9.563122c9d232e9cf53aed6022577af2966e500324c8253889dd3f651e2289ec5";
    private static final String HMAC_INVALID_NO_URL = "eyJleHBpcmVNaWxsaXNlY29uZCI6ICIxNTc2Njk5NTkwNjI2IiwiaGFzaE1lY2hhbmlzbSI6ICJobWFjU0hBMjU2IiwiZW5kcG9pbnRVcmwiOiAiIiwibm9uY2UiOiAiNzBlMGQ5Mzk4M2RjZWU2YjQ5MDc1OWU3ZTYxZjM4ODAifQ==.6563ca568f670504c69bfec6a48a899cbfeb01f903bdbc03020b88853d651ce2";

    private static final String EXPIRED_TIME = "1576699057671";
    private static final String EXPIRED_TIME_INVALID = "abc";
    private static final String NONCE = "nonce";


    private static final String MISSING_HMAC_SIGNATURE = "HMAC signature should not be null or empty";
    private static final String MISSING_SECRET_VALUE = "Secret should not be null or empty";
    private static final String SIGNATURE_EXPIRED = "Signature is expired";
    private static final String INVALID_SIGNATURE = "Invalid signature";
    private static final String MISSING_ATTRIBUTES_IN_SIGNATURE = "Missing url or nonce or expire time in the signature";
    private static final String ERROR_GENERATING_SIGNATURE = "Error generating the signature";

    @Test
    public void should_throwException_when_null_hmac_signature() {
        try {
            sut.verifyHmacSignature(null, SECRET);
            fail("Exception should be thrown");
        } catch (SignatureServiceException e) {
            assertEquals(MISSING_HMAC_SIGNATURE, e.getMessage());
        } catch (Exception e) {
            fail("This Exception should not be thrown");
        }
    }

    @Test
    public void should_throwException_when_Empty_hmac_signature() {
        try {
            sut.verifyHmacSignature("", SECRET);
            fail("Exception should be thrown");
        } catch (SignatureServiceException e) {
            assertEquals(MISSING_HMAC_SIGNATURE, e.getMessage());
        } catch (Exception e) {
            fail("This Exception should not be thrown");
        }
    }

    @Test
    public void should_throwException_when_null_secret() {
        try {
            sut.verifyHmacSignature(HMAC_VALID_EXPIRED, null);
            fail("Exception should be thrown");
        } catch (SignatureServiceException e) {
            assertEquals(MISSING_SECRET_VALUE, e.getMessage());
        } catch (Exception e) {
            fail("This Exception should not be thrown");
        }
    }

    @Test
    public void should_throwException_when_expired_signature() {
        try {
            sut.verifyHmacSignature(HMAC_VALID_EXPIRED, SECRET);
            fail("Exception should be thrown");
        } catch (SignatureServiceException e) {
            assertEquals(SIGNATURE_EXPIRED, e.getMessage());
        } catch (Exception e) {
            fail("This Exception should not be thrown");
        }
    }

    @Test
    public void should_throwException_when_invalid_signature() {
        try {
            sut.verifyHmacSignature(HMAC_INVALID, SECRET);
            fail("Exception should be thrown");
        } catch (SignatureServiceException e) {
            assertEquals(INVALID_SIGNATURE, e.getMessage());
        } catch (Exception e) {
            fail("This Exception should not be thrown");
        }
    }

    @Test
    public void should_throwException_when_no_url_in_signature() {
        try {
            sut.verifyHmacSignature(HMAC_INVALID_NO_URL, SECRET);
            fail("Exception should be thrown");
        } catch (SignatureServiceException e) {
            assertEquals(MISSING_ATTRIBUTES_IN_SIGNATURE, e.getMessage());
        } catch (Exception e) {
            fail("This Exception should not be thrown");
        }
    }


    @Test
    public void should_returnValidSignature_whenUrlAndSecretCorrectlyProvided() throws Exception {
        String testSignature = this.sut.getSignedSignature(ENDPOINT_URL, SECRET);

        String[] tokens = testSignature.split("\\.");
        byte[] dataBytes = Base64.getDecoder().decode(tokens[0]);
        String requestSignature = tokens[1];

        String data = new String(dataBytes, "UTF-8");
        Gson gson = new Gson();
        HmacData hmacData = gson.fromJson(data, HmacData.class);
        String url = hmacData.getEndpointUrl();
        String nonce = hmacData.getNonce();
        String expireTime = hmacData.getExpireMillisecond();
        if (url == null || nonce == null || expireTime == null || url.isEmpty() || nonce.isEmpty() || expireTime.isEmpty()) {
            fail("should get url, url and nonce from signature");
        }
        Assert.assertEquals(ENDPOINT_URL, url);
        Assert.assertTrue(System.currentTimeMillis() < Long.parseLong(expireTime));
        String newSignature = sut.getSignedSignature(url, SECRET, expireTime, nonce);
        Assert.assertTrue(requestSignature.equalsIgnoreCase(newSignature));
    }

    @Test
    public void should_VerifiedSignature_whenUrlAndSecretCorrectlyProvided() {
        try {
            String testSignature = sut.getSignedSignature(ENDPOINT_URL, SECRET);
            sut.verifyHmacSignature(testSignature, SECRET);
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    public void should_ThrowException_whenDifferentSecretProvided() {
        try {
            String testSignature = sut.getSignedSignature(ENDPOINT_URL, SECRET);
            sut.verifyHmacSignature(testSignature, ANOTHER_SECRET);
        } catch (SignatureServiceException e) {
            Assert.assertEquals(INVALID_SIGNATURE, e.getMessage());
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    public void should_throwException_whenUrlIsNotProvided() {
        try {
            this.sut.getSignedSignature(null, SECRET);
            fail("should throw exception");
        } catch (SignatureServiceException e) {
            Assert.assertEquals(ERROR_GENERATING_SIGNATURE, e.getMessage());
        } catch (Exception ex) {
            fail("shouldn't throw any other exceptions");
        }
    }

    @Test
    public void should_throwException_whenSecretIsNotProvided() {
        try {
            this.sut.getSignedSignature(ENDPOINT_URL, null);
            fail("should throw exception");
        } catch (SignatureServiceException e) {
            Assert.assertEquals(ERROR_GENERATING_SIGNATURE, e.getMessage());
        } catch (Exception ex) {
            fail("shouldn't throw any other exceptions");
        }
    }

    @Test
    public void should_throwException_whenInappropriateSecretProvided() {
        try {
            this.sut.getSignedSignature(ENDPOINT_URL, "(*&^#$%&");
            fail("should throw exception");
        } catch (SignatureServiceException e) {
            Assert.assertEquals(ERROR_GENERATING_SIGNATURE, e.getMessage());
        } catch (Exception ex) {
            fail("shouldn't throw any other exceptions");
        }
    }

    @Test
    public void should_throwException_whenSecretIsNotProvidedInGetSignature() {
        try {
            this.sut.getSignedSignature(ENDPOINT_URL, null, EXPIRED_TIME, NONCE);
            fail("should throw exception");
        } catch (SignatureServiceException e) {
            Assert.assertEquals(ERROR_GENERATING_SIGNATURE, e.getMessage());
        } catch (Exception ex) {
            fail("shouldn't throw any other exceptions");
        }
    }

    @Test
    public void should_throwException_whenUrlIsNotProvidedInGetSignature() {
        try {
            this.sut.getSignedSignature(null, SECRET, EXPIRED_TIME, NONCE);
            fail("should throw exception");
        } catch (SignatureServiceException e) {
            Assert.assertEquals(ERROR_GENERATING_SIGNATURE, e.getMessage());
        } catch (Exception ex) {
            fail("shouldn't throw any other exceptions");
        }
    }

    @Test
    public void should_throwException_whenInvalidTimeProvidedInGetSignature() {
        try {
            this.sut.getSignedSignature(ENDPOINT_URL, SECRET, EXPIRED_TIME_INVALID, NONCE);
            fail("should throw exception");
        } catch (SignatureServiceException e) {
            Assert.assertEquals(ERROR_GENERATING_SIGNATURE, e.getMessage());
        } catch (Exception ex) {
            fail("shouldn't throw any other exceptions");
        }
    }
}