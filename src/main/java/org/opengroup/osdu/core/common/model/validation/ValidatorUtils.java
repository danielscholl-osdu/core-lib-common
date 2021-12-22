package org.opengroup.osdu.core.common.model.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidatorUtils {
  public static String escapeString(String message) {
    return message.replace("\\","\\\\")
                    .replace("{","\\{")
                    .replace("}","\\}")
                    .replace("$","\\$");
  }
}