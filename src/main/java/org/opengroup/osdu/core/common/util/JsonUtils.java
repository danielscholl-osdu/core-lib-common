package org.opengroup.osdu.core.common.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class JsonUtils {

    public static String jsonElementToString(JsonElement jsonElement) {
        if (jsonElement == null) {
            throw new IllegalArgumentException("Input json element is null");
        }

        if (jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsString();
        } else if (jsonElement.isJsonArray()) {
            if (((JsonArray) jsonElement).size() == 1) {
                return jsonElement.getAsString();
            }
        }

        return jsonElement.toString();
    }
}