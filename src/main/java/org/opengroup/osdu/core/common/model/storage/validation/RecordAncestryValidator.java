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

package org.opengroup.osdu.core.common.model.storage.validation;

import org.opengroup.osdu.core.common.model.storage.RecordAncestry;
import org.opengroup.osdu.core.common.model.validation.ValidatorUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecordAncestryValidator implements ConstraintValidator<ValidRecordAncestry, RecordAncestry> {
    @Override
    public void initialize(ValidRecordAncestry constraintAnnotation) {
        // do nothing
    }

    @Override
    public boolean isValid(RecordAncestry recordAncestry, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (recordAncestry != null) {
            if (recordAncestry.getParents() == null) {
                context.buildConstraintViolationWithTemplate(ValidationDoc.INVALID_PAYLOAD)
                        .addConstraintViolation();
                return false;
            }
            Set<String> recordIds = recordAncestry.getParents();
            for (String recordId : recordIds) {
                if (!recordId.matches(ValidationDoc.RECORD_ID_REGEX)) {
                    context.buildConstraintViolationWithTemplate(String.format(ValidationDoc.INVALID_PARENT_RECORD_ID_FORMAT, ValidatorUtils.escapeString(recordId)))
                            .addConstraintViolation();
                    return false;
                }

                if (!recordId.matches(ValidationDoc.RECORD_ID_WITH_VERSION_REGEX)) {
                    context.buildConstraintViolationWithTemplate(String.format(ValidationDoc.INVALID_PARENT_RECORD_VERSION_FORMAT, ValidatorUtils.escapeString(recordId)))
                            .addConstraintViolation();
                    return false;
                }
            }
        }
        return true;
    }
}
