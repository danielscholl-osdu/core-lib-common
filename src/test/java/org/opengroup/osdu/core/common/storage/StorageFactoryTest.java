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

package org.opengroup.osdu.core.common.storage;

import org.junit.Test;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;

import static org.junit.Assert.assertNotNull;

public class StorageFactoryTest {
    @Test
    public void constructorTest_when_non_null_config_should_not_throw_exception() {
        StorageAPIConfig config = new StorageAPIConfig("any url", "any key");
        StorageFactory storageFactory = new StorageFactory(config, null);
        assertNotNull(storageFactory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorTest_when_null_config_should_throw_exception() {
        StorageFactory storageFactory = new StorageFactory(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void createStorageService_instance_when_null_dps_headers_should_throw_exception() {
        StorageAPIConfig config = new StorageAPIConfig("any url", "any key");
        StorageFactory storageFactory = new StorageFactory(config, null);
        assertNotNull(storageFactory);
        storageFactory.create(null);
    }

    @Test
    public void createStorageService_instance_when_not_null_dps_headers_should_not_throw_exception() {
        StorageAPIConfig config = new StorageAPIConfig("any url", "any key");
        StorageFactory storageFactory = new StorageFactory(config, null);
        assertNotNull(storageFactory);
        IStorageService storageService = storageFactory.create(new DpsHeaders());
        assertNotNull(storageService);
    }
}