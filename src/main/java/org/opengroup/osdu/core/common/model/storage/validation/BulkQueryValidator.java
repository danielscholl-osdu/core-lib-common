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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.opengroup.osdu.core.common.model.validation.ValidatorUtils;
import org.opengroup.osdu.core.common.model.storage.RecordQuery;

public class BulkQueryValidator implements ConstraintValidator<ValidBulkQuery, RecordQuery> {

    @Override
    public void initialize(ValidBulkQuery constraintAnnotation) {
        // do nothing
    }

    @Override
    public boolean isValid(RecordQuery recordQuery, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if(recordQuery == null){
            context.buildConstraintViolationWithTemplate(ValidationDoc.INVALID_PAYLOAD)
                    .addConstraintViolation();
            return false;
        }

        List<String> recordIds = recordQuery.getIds();
        Set<String> ids = new HashSet<>();
        for (String recordId : recordIds) {
            if (ids.contains(recordId)) {
                context.buildConstraintViolationWithTemplate(String.format(ValidationDoc.DUPLICATE_RECORD_ID, ValidatorUtils.escapeString(recordId)))
                        .addConstraintViolation();
                return false;
            }
            if (!recordId.matches(ValidationDoc.RECORD_ID_REGEX) && !recordId.matches(ValidationDoc.RECORD_ID_WITH_VERSION_REGEX)) {
                context.buildConstraintViolationWithTemplate(String.format(ValidationDoc.INVALID_RECORD_ID_FORMAT, ValidatorUtils.escapeString(recordId)))
                        .addConstraintViolation();
                return false;
            }
            ids.add(recordId);
        }
        return true;
    }
}