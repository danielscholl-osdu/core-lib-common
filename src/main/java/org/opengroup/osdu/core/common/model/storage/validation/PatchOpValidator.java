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

import java.util.function.Predicate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.opengroup.osdu.core.common.model.storage.PatchOperation;

public class PatchOpValidator implements ConstraintValidator<ValidPatchOp, PatchOperation> {
    private static final String OPERATION_ADD = "add";
    private static final String OPERATION_REMOVE = "remove";
    private static final String OPERATION_REPLACE = "replace";

    @Override
    public void initialize(ValidPatchOp constraintAnnotation) {
        // do nothing
    }

    @Override
    public boolean isValid(PatchOperation operation, ConstraintValidatorContext context) {
        Predicate<String> allowedOperations = operation.getPath().startsWith("/tags") ?
            allowedOperationsForTagsPredicate() :
            allowedOperationsForOthersPredicate();

        if (!allowedOperations.test(operation.getOp())) {
            context.buildConstraintViolationWithTemplate(ValidationDoc.INVALID_PATCH_OPERATION).addConstraintViolation();
            return false;
        }
        return true;
    }

    private Predicate<String> allowedOperationsForTagsPredicate() {
        return operation -> OPERATION_ADD.equals(operation) ||
            OPERATION_REMOVE.equals(operation) ||
            OPERATION_REPLACE.equals(operation);
    }


    private Predicate<String> allowedOperationsForOthersPredicate() {
      return operation -> OPERATION_REPLACE.equals(operation);
    }
}
