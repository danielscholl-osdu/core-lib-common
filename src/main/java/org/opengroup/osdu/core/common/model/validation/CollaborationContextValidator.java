// Copyright 2022 Schlumberger
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

package org.opengroup.osdu.core.common.model.validation;

import com.google.common.base.Strings;
import org.opengroup.osdu.core.common.Constants;
import org.opengroup.osdu.core.common.model.collaboration.validation.CollaborationContextValidationDoc;
import org.opengroup.osdu.core.common.util.CollaborationContextUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashMap;
import java.util.Map;

import static org.opengroup.osdu.core.common.model.collaboration.validation.CollaborationContextValidationDoc.DIRECTIVE_FORMAT;
import static org.opengroup.osdu.core.common.model.collaboration.validation.CollaborationContextValidationDoc.DIRECTIVE_KEY_ALLOWED_CHARACTERS;

public class CollaborationContextValidator implements ConstraintValidator<ValidateCollaborationContext, String> {

    @Override
    public boolean isValid(String collaborationDirective, ConstraintValidatorContext constraintValidatorContext) {
        constraintValidatorContext.disableDefaultConstraintViolation();
        return validateCollaborationProperties(collaborationDirective, constraintValidatorContext);
    }

    private boolean validateCollaborationProperties(String collaborationDirective, ConstraintValidatorContext constraintValidatorContext) {

        if (collaborationDirective == null) {
            return true;
        }
        if (collaborationDirective.isEmpty()) {
            constraintValidatorContext.buildConstraintViolationWithTemplate(CollaborationContextValidationDoc.COLLABORATION_CONTEXT_EMPTY).addConstraintViolation();
            return false;
        }

        if(!validateDirectives(collaborationDirective, constraintValidatorContext))
            return false;
        Map<String, String> directiveProperties = CollaborationContextUtil.getCollaborationDirectiveProperties(collaborationDirective);

        if (Strings.isNullOrEmpty(directiveProperties.get(Constants.ID))) {
            constraintValidatorContext.buildConstraintViolationWithTemplate(CollaborationContextValidationDoc.COLLABORATION_CONTEXT_MANDATORY_DIRECTIVES).addConstraintViolation();
            return false;
        }

        if (Strings.isNullOrEmpty(directiveProperties.get(Constants.APPLICATION))) {
            constraintValidatorContext.buildConstraintViolationWithTemplate(CollaborationContextValidationDoc.COLLABORATION_CONTEXT_MANDATORY_DIRECTIVES).addConstraintViolation();
            return false;
        }

        if (!directiveProperties.get(Constants.ID).matches(CollaborationContextValidationDoc.COLLABORATION_ID_PATTERN)) {
            constraintValidatorContext.buildConstraintViolationWithTemplate(
                    String.format(CollaborationContextValidationDoc.INVALID_ID_DIRECTIVE, ValidatorUtils.escapeString(directiveProperties.get(Constants.ID)))).addConstraintViolation();
            return false;
        }
        if (!directiveProperties.get(Constants.APPLICATION).matches(CollaborationContextValidationDoc.COLLABORATION_APPLICATION_PATTERN)) {
            constraintValidatorContext.buildConstraintViolationWithTemplate(
                    String.format(CollaborationContextValidationDoc.INVALID_APPLICATION_DIRECTIVE, ValidatorUtils.escapeString(directiveProperties.get(Constants.APPLICATION)))).addConstraintViolation();
            return false;
        }

        return true;
    }

    private boolean validateDirectives(String collaborationDirective, ConstraintValidatorContext constraintValidatorContext) {
        Map<String, String> collaborationDirectiveProperties = new HashMap<>();
        String[] directives = collaborationDirective.split(",");
        for(String directive : directives) {
            String[] keyValue = directive.split("=");
            if (keyValue.length != 2) {
                constraintValidatorContext.buildConstraintViolationWithTemplate(DIRECTIVE_FORMAT).addConstraintViolation();
                return false;
            }
            if (collaborationDirectiveProperties.containsKey(keyValue[0].toLowerCase().trim())) {
                constraintValidatorContext.buildConstraintViolationWithTemplate("Directive " + keyValue[0] + " provided more than one time").addConstraintViolation();
                return false;
            }
            collaborationDirectiveProperties.put(keyValue[0].toLowerCase().trim(), keyValue[1].trim());
        }

        for (Map.Entry<String, String> directiveKeyValue : collaborationDirectiveProperties.entrySet()) {

            if (Strings.isNullOrEmpty(directiveKeyValue.getKey()) || Strings.isNullOrEmpty(directiveKeyValue.getValue())) {
                constraintValidatorContext.buildConstraintViolationWithTemplate(DIRECTIVE_FORMAT).addConstraintViolation();
                return false;
            }
            if (!directiveKeyValue.getKey().matches(CollaborationContextValidationDoc.DIRECTIVE_KEY_PATTERN)) {
                constraintValidatorContext.buildConstraintViolationWithTemplate(DIRECTIVE_KEY_ALLOWED_CHARACTERS).addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
