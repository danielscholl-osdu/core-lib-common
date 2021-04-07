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

package org.opengroup.osdu.core.common.model.http;

import com.google.common.base.Strings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = false)
public class AppException extends RuntimeException {

    @Getter
    private AppError error;

    @Getter
    private Exception originalException;

    public AppError getError() {
        return this.error;
    }

    public AppException(int status, String reason, String message) {
        this(status, reason, message, null, null, null);
    }

    public AppException(int status, String reason, String message, String[] errors) {
        this(status, reason, message, null, null, errors);
    }


    public AppException(int status, String reason, String message, String debuggingInfo) {
        this(status, reason, message, debuggingInfo, null, null);
    }

    public AppException(int status, String reason, String message, String[] errors, String debuggingInfo) {
        this(status, reason, message, debuggingInfo, null, errors);
    }

    public AppException(int status, String reason, String message, Exception originalException) {
        this(status, reason, message, null, originalException, null);
   }

    public AppException(int status, String reason, String message, String[] errors, Exception originalException) {
        this(status, reason, message, null, originalException, errors);
    }

    public AppException(int status, String reason, String message, String debuggingInfo, Exception originalException) {
        this(status, reason, message, debuggingInfo, originalException, null);
    }

    public AppException(int status, String reason, String message, String debuggingInfo, Exception originalException, String[] errors) {
        super(sanitizeString(message), originalException);
        String sanitizedReason = sanitizeString(reason);

        this.originalException = originalException;

        this.error = AppError.builder()
                .code(status)
                .reason(sanitizedReason)
                .message(this.getMessage())
                .debuggingInfo(debuggingInfo)
                .originalException(originalException)
                .errors(errors).build();
    }

    public static AppException createForbidden(String debuggingInfo) {
        return new AppException(HttpStatus.SC_FORBIDDEN, "Access denied", "The user is not authorized to perform this action", debuggingInfo);
    }

    public static AppException createUnauthorized(String debuggingInfo) {
        return new AppException(HttpStatus.SC_UNAUTHORIZED, "Unauthorized", "The user is not authorized to perform this action", debuggingInfo);
    }

    public static AppException createForbidden(){
        return new AppException(403, "Forbidden", "The user is not authorized to perform this action");
    }

    public static AppException legalTagAlreadyExistsError(String name){
        return new AppException(409, "Conflict", "A LegalTag already exists for the given name " + name);
    }

    public static AppException legalTagDoesNotExistError(String name){
        return new AppException(404, "Not found", "Cannot update a LegalTag that does not exist for given name " + name);
    }

    public static AppException countryCodeLoadingError(){
        return new AppException(500, "Internal Server Error", "Unexpected error. Please wait 30 seconds and try again.");
    }

    private static String sanitizeString(String msg) {
        return Strings.isNullOrEmpty(msg) ? "" : msg.replace('\n', '_').replace('\r', '_');
    }
}
