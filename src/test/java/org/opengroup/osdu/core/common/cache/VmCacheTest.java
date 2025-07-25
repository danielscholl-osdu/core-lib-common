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

package org.opengroup.osdu.core.common.cache;

import org.junit.Test;
import org.opengroup.osdu.core.common.cache.enums.CachingStrategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class VmCacheTest {
    @Test
    public void should_returnCachedItem_andThen_returnUpdateItem_andThen_notReturnDeletedItem() {

        String id = "1";
        String value = "abc";
        VmCache<String, String> sut = new VmCache<>(2, 2);

        assertNull(sut.get(id));

        sut.put(id, value);
        assertEquals(value, sut.get(id));

        sut.put(id, "newVal");
        assertEquals("newVal", sut.get(id));

        sut.delete(id);
        assertNull(sut.get(id));
    }

    @Test
    public void should_invalidateItem_after_expirationHasPassed() throws InterruptedException {
        String id = "1";
        String value = "abc";
        VmCache<String, String> sut = new VmCache<>(1, 1);

        sut.put(id, value);
        assertEquals(value, sut.get(id));
        Thread.sleep(1010);

        assertNull(sut.get(id));
    }

    @Test
    public void should_overwriteItems_after_cacheLimitIsReached() {
        String id = "1";
        String value = "abc";
        VmCache<String, String> sut = new VmCache<>(1, 1);

        sut.put(id, value);
        assertEquals(value, sut.get(id));

        sut.put("new", "value");

        assertNull(sut.get(id));
    }

    @Test
    public void should_returnCachedItem_when_itHasBeenCleared() {

        String id = "1";
        String value = "abc";
        VmCache<String, String> sut = new VmCache<>(2, 2);
        sut.put(id, value);
        assertEquals(value, sut.get(id));

        sut.clearAll();

        assertNull(sut.get(id));
    }

    @Test
    public void shouldInvalidateItem_using_expireAfterWriteStrategy() throws InterruptedException {
        String id = "1";
        String value = "abc";
        VmCache<String, String> sut = new VmCache<>(1, 1, CachingStrategy.EXPIRE_AFTER_WRITE);

        // Item is inserted into cache..
        sut.put(id, value);
        Thread.sleep(505);

        // Item is available within expiry period.
        assertEquals(value, sut.get(id));

        Thread.sleep(505);

        // item is not available post expiry period
        assertNull(sut.get(id));
    }

    @Test
    public void shouldInvalidateItem_using_expireAfterAccessStrategy() throws InterruptedException {
        String id = "1";
        String value = "abc";
        VmCache<String, String> sut = new VmCache<>(1, 1, CachingStrategy.EXPIRE_AFTER_ACCESS);

        // Item is inserted into cache..
        sut.put(id, value);
        Thread.sleep(505);

        // Item is available within expiry period. This access refreshes the expiry back to 1000 ms
        assertEquals(value, sut.get(id));
        Thread.sleep(505);

        // Item is available even after 1 second. Because last access was 500ms back.
        assertEquals(value, sut.get(id));
        Thread.sleep(1010);

        // After 1 second without access, item is no longer available.
        assertNull(sut.get(id));
    }
}
