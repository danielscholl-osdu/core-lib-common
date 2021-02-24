// Copyright © Schlumberger
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

package org.opengroup.osdu.core.common.policy;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.opengroup.osdu.core.common.http.HttpResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PolicyExceptionTest {

    @Test
    public void constructorTest() {
        PolicyException exception = new PolicyException("unknown error", new HttpResponse());
        assertNotNull(exception);

        String errorMessage = exception.getMessage();
        assertNotNull(errorMessage);
        assertEquals("unknown error", errorMessage);
    }

    @Test
    public void constructorExceptionTest() {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setResponseCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        httpResponse.setException(new Exception("exception"));
        PolicyException policyException = new PolicyException("unknown error", httpResponse);
        assertNotNull(policyException);

        String errorMessage = policyException.getMessage();
        assertNotNull(errorMessage);
        assertEquals("unknown error", errorMessage);
        assertEquals("exception", policyException.getHttpResponse().getException().getMessage());
    }
}
