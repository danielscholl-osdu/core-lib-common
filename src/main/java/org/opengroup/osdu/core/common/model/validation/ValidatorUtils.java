package org.opengroup.osdu.core.common.model.validation;

public class ValidatorUtils {
  public static String escapeString(String message) {
    return message.replace("\\","\\\\")
                    .replace("{","\\{")
                    .replace("}","\\}")
                    .replace("$","\\$");
  }
}