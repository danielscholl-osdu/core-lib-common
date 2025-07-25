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

package org.opengroup.osdu.core.common.model.storage;

import org.opengroup.osdu.core.common.model.http.DpsException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.opengroup.osdu.core.common.http.HttpResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StorageException extends DpsException {

    private static final long serialVersionUID = -3823738766134121467L;

    public StorageException(String message, HttpResponse httpResponse) {
        super(message, httpResponse);
    }
}