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

import org.apache.commons.lang3.NotImplementedException;

public interface ICache<K, O> {

    void put(K k, O o);

    O get(K k);

    void delete(K k);

    void clearAll();

    /**
     * Increment and decrement operation expects O to be numerical (integral) value
     */
    default Long increment(K key) {
        throw new NotImplementedException("Method not implemented!");
    }

    default Long decrement(K key) {
        throw new NotImplementedException("Method not implemented!");
    }
}