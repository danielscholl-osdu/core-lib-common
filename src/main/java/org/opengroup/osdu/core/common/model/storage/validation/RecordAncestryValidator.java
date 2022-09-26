package org.opengroup.osdu.core.common.model.storage.validation;

import org.opengroup.osdu.core.common.model.storage.RecordAncestry;
import org.opengroup.osdu.core.common.model.validation.ValidatorUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
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

        if(recordAncestry == null){
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
        return true;
    }
}
