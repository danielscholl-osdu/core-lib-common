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

package org.opengroup.osdu.core.common.notification;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opengroup.osdu.core.common.http.HttpResponse;
import org.opengroup.osdu.core.common.model.http.DpsException;

@Data
@EqualsAndHashCode(callSuper = false)
public class SubscriptionException extends DpsException {

    private static final long serialVersionUID = -3557182069722613408L;

    public SubscriptionException(String message, HttpResponse httpResponse) {
        super(message, httpResponse);
    }

    public SubscriptionException(String message, HttpResponse httpResponse, Exception ex) {
        super(message, httpResponse);
        this.initCause(ex);
    }
}
