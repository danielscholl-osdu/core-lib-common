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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.opengroup.osdu.core.common.model.storage.PatchOperation;

public class PatchOpValidator implements ConstraintValidator<ValidPatchOp, PatchOperation> {
    private static final String OPERATION_ADD = "add";
    private static final String OPERATION_REMOVE = "remove";
    private static final String OPERATION_REPLACE = "replace";
    private static final Set<String> VALID_PATHS_FOR_PATCH = new HashSet<>(Arrays.asList("tags", "acl", "legal"));

    @Override
    public void initialize(ValidPatchOp constraintAnnotation) {
        // do nothing
    }

    @Override
    public boolean isValid(PatchOperation operation, ConstraintValidatorContext context) {

        Predicate<String> allowedOperations = null;

        if (operation == null || operation.getPath() == null) {
            context.buildConstraintViolationWithTemplate(ValidationDoc.INVALID_PATCH_PATH).addConstraintViolation();
            return false;
        }

        String[] pathComponent = operation.getPath().split("/");
        String firstPathComponent = pathComponent[1];

        if (VALID_PATHS_FOR_PATCH.contains(firstPathComponent)) {
            allowedOperations = allowedOperationsForTagsAclLegal();
            if (!allowedOperations.test(operation.getOp())) {
                context.buildConstraintViolationWithTemplate(ValidationDoc.INVALID_PATCH_OPERATION).addConstraintViolation();
                return false;
            }
        } else {
            context.buildConstraintViolationWithTemplate(ValidationDoc.INVALID_PATCH_OPERATION).addConstraintViolation();
            return false;
        }
        return true;
    }

    private Predicate<String> allowedOperationsForTagsAclLegal() {
        return operation -> OPERATION_ADD.equals(operation) ||
                OPERATION_REMOVE.equals(operation) ||
                OPERATION_REPLACE.equals(operation);
    }

}
