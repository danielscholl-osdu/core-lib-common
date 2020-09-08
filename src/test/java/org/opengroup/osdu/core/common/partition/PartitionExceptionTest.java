// Copyright 2017-2020, Schlumberger
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

package org.opengroup.osdu.core.common.partition;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PartitionExceptionTest {

    @Test
    public void constructorTest() {
        PartitionException exception = new PartitionException("unknown error");
        assertNotNull(exception);

        String errorMessage = exception.getMessage();
        assertNotNull(errorMessage);
        assertEquals("unknown error", errorMessage);
    }

    @Test
    public void constructorExceptionTest() {
        PartitionException partitionException = new PartitionException("unknown error", new Exception("exception"));
        assertNotNull(partitionException);

        String errorMessage = partitionException.getMessage();
        Throwable exception = partitionException.getCause();
        assertNotNull(errorMessage);
        assertEquals("unknown error", errorMessage);
        assertEquals("exception", exception.getMessage());
    }
}
