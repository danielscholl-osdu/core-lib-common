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

package org.opengroup.osdu.core.common.partition;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PartitionException extends Exception {

    private static final long serialVersionUID = 9094949225576291097L;

    public PartitionException(String message) {
        super(message);
    }

    /**
     * Exception defined for PartitionService.
     * It is is not a DPS Exception since it is only used for cacheHttpClient
     *
     * @param message
     * @param cause
     */
    public PartitionException(String message, Throwable cause) {
        super(message, cause);
    }
}