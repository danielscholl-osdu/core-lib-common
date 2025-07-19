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

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opengroup.osdu.core.common.http.HttpResponse;
import org.opengroup.osdu.core.common.model.http.DpsException;

@Data
@EqualsAndHashCode(callSuper = false)
public class PartitionException extends DpsException {

    private static final long serialVersionUID = 9094949225576291097L;

    /**
     * Exception defined for PartitionService.
     *
     * @param message
     * @param httpResponse
     */
    public PartitionException(String message, HttpResponse httpResponse) {
        super(message, httpResponse);
    }
}