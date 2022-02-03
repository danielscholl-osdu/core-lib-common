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
