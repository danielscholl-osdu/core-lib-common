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

package org.opengroup.osdu.core.common.logging;

import org.opengroup.osdu.core.common.model.http.Request;
import org.opengroup.osdu.core.common.logging.audit.AuditPayload;

import java.util.Map;

public interface ILogger extends AutoCloseable {
    void audit(String logPrefix, AuditPayload payload, Map<String, String> headers);

    default void audit(final String loggerName, final String logPrefix, final AuditPayload payload,
                       final Map<String, String> headers) {
        this.audit(logPrefix, payload, headers);
    }

    void request(String logPrefix, Request request, Map<String, String> headers);

    default void request(final String loggerName, final String logPrefix, final Request request,
                         final Map<String, String> headers) {
        this.request(logPrefix, request, headers);
    }

    void info(String logPrefix, String message, Map<String, String> headers);

    default void info(final String loggerName, final String logPrefix, final String message,
                      final Map<String, String> headers) {
        this.info(logPrefix, message, headers);
    }

    void warning(String logPrefix, String message, Map<String, String> headers);

    default void warning(final String loggerName, final String logPrefix, final String message,
                         final Map<String, String> headers) {
        this.warning(logPrefix, message, headers);
    }

    void warning(String logPrefix, String message, Exception ex, Map<String, String> headers);

    default void warning(final String loggerName, final String logPrefix, final String message, final Exception ex,
                         final Map<String, String> headers) {
        this.warning(logPrefix, message, ex, headers);
    }

    void error(String logPrefix, String message, Map<String, String> headers);

    default void error(final String loggerName, final String logPrefix, final String message,
                       final Map<String, String> headers) {
        this.error(logPrefix, message, headers);
    }

    void error(String logPrefix, String message, Exception ex, Map<String, String> headers);

    default void error(final String loggerName, final String logPrefix, final String message, final Exception ex,
                       final Map<String, String> headers) {
        this.error(logPrefix, message, ex, headers);
    }
}
