// Copyright 2017-2019, Schlumberger
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

package org.opengroup.osdu.core.common.http;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class HttpRequestTest {
    @Test
    public void should_setDefaults_when_creatingInstance() {
        HttpRequest sut = HttpRequest.get().url("http://google.com").build();
        assertEquals("http://google.com", sut.getUrl());
        assertEquals("GET", sut.getHttpMethod());
        assertEquals(60000, sut.getConnectionTimeout());
        assertEquals(60000, sut.getConnectionRequestTimeout());
        assertEquals(60000, sut.getSocketTimeout());
        assertTrue(sut.isFollowRedirects());
    }

}
