// Copyright 2017-2022, Schlumberger
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

package org.opengroup.osdu.core.common.util;

import com.google.gson.Gson;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class KindParserTest {
    private class FakeQuery {
        public Object kind;
    }

    @Test
    public void validTypeOfMonoKind() {
        Gson gson = new Gson();
        String jsonText = "{ \"kind\": \"a:s:e:1.0.0\" }";
        FakeQuery query = gson.fromJson(jsonText, FakeQuery.class);
        List<String> kinds = KindParser.parse(query.kind);
        assertEquals(1, kinds.size());
        assertEquals("a:s:e:1.0.0", kinds.get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidTypeOfMonoKind() {
        Gson gson = new Gson();
        String jsonText = "{ \"kind\": 123 }";
        FakeQuery query = gson.fromJson(jsonText, FakeQuery.class);
        KindParser.parse(query.kind);
    }

    @Test
    public void validTypeOfMultiKinds() {
        Gson gson = new Gson();
        String jsonText = "{ \"kind\": [\"a:s:e:1.0.0\", \"a:s:e:2.0.0\"] }";
        FakeQuery query = gson.fromJson(jsonText, FakeQuery.class);
        List<String> kinds = KindParser.parse(query.kind);
        assertEquals(2, kinds.size());
        assertEquals("a:s:e:1.0.0", kinds.get(0));
        assertEquals("a:s:e:2.0.0", kinds.get(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidTypeOfMultiKinds() {
        Gson gson = new Gson();
        String jsonText = "{ \"kind\": [123, 456] }";
        FakeQuery query = gson.fromJson(jsonText, FakeQuery.class);
        assertNotNull(query);
        assertNotNull(query.kind);
        KindParser.parse(query.kind);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidMixedTypeOfMultiKinds() {
        Gson gson = new Gson();
        String jsonText = "{ \"kind\": [\"a:s:e:1.0.0\", 123] }";
        FakeQuery query = gson.fromJson(jsonText, FakeQuery.class);
        assertNotNull(query);
        assertNotNull(query.kind);
        KindParser.parse(query.kind);
    }
}
