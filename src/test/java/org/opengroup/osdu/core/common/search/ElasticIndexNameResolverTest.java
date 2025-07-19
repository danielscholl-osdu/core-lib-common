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

package org.opengroup.osdu.core.common.search;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ElasticIndexNameResolverTest {

    @InjectMocks
    private ElasticIndexNameResolver sut;

    @Before
    public void setup() {
        this.sut.getIndexNameFromKind("tenant1:welldb-v2:wellbore:2.0.0");
    }

    @Test
    public void test_correct_index_from_kind() {
        assertEquals("tenant1-welldb-v2-wellbore-2.0.0", this.sut.getIndexNameFromKind("tenant1:welldb-v2:wellbore:2.0.0"));
    }

    @Test
    public void test_correct_kind_from_index() {
        assertEquals("tenant1:welldb-v2:wellbore:2.0.0", this.sut.getKindFromIndexName("tenant1-welldb-v2-wellbore-2.0.0"));
    }

    @Test
    public void has_index_name_alias() {
        assertTrue(this.sut.isIndexAliasSupported("tenant1:welldb-v2:wellbore:2.0.0"));
        assertTrue(this.sut.isIndexAliasSupported("tenant1:welldb-v2:wellbore:2.*.*"));
    }

    @Test
    public void has_not_index_name_alias() {
        assertFalse(this.sut.isIndexAliasSupported("tenant1:welldb-v2:wellbore:2.*"));
        assertFalse(this.sut.isIndexAliasSupported("tenant1:welldb-v2:wellbore:2.*.0"));
        assertFalse(this.sut.isIndexAliasSupported("tenant1:welldb-v2:wellbore:2.0.*"));
        assertFalse(this.sut.isIndexAliasSupported("tenant1:welldb-v2:wellbore:*.0.0"));
        assertFalse(this.sut.isIndexAliasSupported("tenant1:welldb-v2:wellbore:*.*.0"));
        assertFalse(this.sut.isIndexAliasSupported("tenant1:welldb-v2:wellbore:*.*.*"));
    }

    @Test
    public void test_index_name_alias() {
        assertEquals("a-1640400723", this.sut.getIndexAliasFromKind("tenant1:welldb-v2:wellbore:2.0.0"));
        assertEquals("a-1640406495", this.sut.getIndexAliasFromKind("tenant1:welldb-v2:wellbore:2.*.*"));
        assertNull(this.sut.getIndexAliasFromKind("tenant1:welldb-v2:wellbore:2.*"));
        assertNull(this.sut.getIndexAliasFromKind("tenant1:welldb-v2:wellbore:2.0.*"));
    }
}
