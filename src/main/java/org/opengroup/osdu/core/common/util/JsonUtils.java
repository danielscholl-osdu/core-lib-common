// Copyright 2021 Schlumberger
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

package org.opengroup.osdu.core.common.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

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

    /**
     * @param propertyName - property name with path split by dots e.g. depth.value
     * @param jsonObject   - JsonObject which presumably contains the property
     * @return JsonElement with property, if it found and null if not
     */
    public static List<JsonElement> getJsonPropertyValueFromJsonObject(String propertyName, JsonObject jsonObject) {
        String[] propertiesHierarchy = splitJsonPropertiesByDots(propertyName);
        // check if the first element ends with ']', if so, go to helper method;
        // return the result coming from helper method.
        JsonObject json = jsonObject;
        List<JsonElement> result = new ArrayList<>();

        for (int i = 0; i < propertiesHierarchy.length; i++) {
            if (i == propertiesHierarchy.length - 1) {
                result.add(json.get(propertiesHierarchy[i]));
                return result;
            } else {
                JsonElement element = json.get(propertiesHierarchy[i]);
                if (element == null || !element.isJsonObject()) {
                    return null;
                }
                json = element.getAsJsonObject();
            }
        }
        return null;
    }

    /**
     * @param propertyName - property name with path split by dots e.g. depth.value
     * @param jsonObject   - JsonObject which presumably contains the property
     * @return true if property found and false if not
     */
    public static boolean isJsonPropertyPresentedInJsonObject(String propertyName, JsonObject jsonObject) {
        String[] propertiesHierarchy = splitJsonPropertiesByDots(propertyName);
        JsonObject json = jsonObject;
        for (int i = 0; i < propertiesHierarchy.length; i++) {
            if (i == propertiesHierarchy.length - 1) {
                return !(json.get(propertiesHierarchy[i]) == null);
            } else {
                JsonElement element = json.get(propertiesHierarchy[i]);
                if (element == null || !element.isJsonObject()) {
                    return false;
                }
                json = element.getAsJsonObject();
            }
        }
        return true;
    }

    /**
     * @param propertyName - property name with path split by dots e.g. depth.value
     * @param value        - the value of the property with Number type
     * @param jsonObject   - JsonObject which presumably contains the property
     */
    public static void overrideNumberPropertyOfJsonObject(String propertyName, List<Number> value, JsonObject jsonObject) {
        String[] nestedNames = splitJsonPropertiesByDots(propertyName);

        if (nestedNames[0].endsWith("]")) {
            overrideNestedNumberPropertyOfJsonObject(nestedNames, value, jsonObject);
        }
        // TODO: check the size of converted values
        JsonObject targetJsonObject = buildNewJsonObject(nestedNames, jsonObject);

        ofNullable(targetJsonObject)
            .ifPresent(json -> json.addProperty(nestedNames[nestedNames.length - 1], value.get(0)));
    }

    private static void overrideNestedNumberPropertyOfJsonObject(String[] nestedNames, List<Number> values, JsonObject jsonObject) {
        JsonArray elementArray = jsonObject.getAsJsonArray(nestedNames[0]);
        String[] innerNestedNames = new String[nestedNames.length - 1];
        System.arraycopy(nestedNames, 1, innerNestedNames, 0, nestedNames.length - 1);
        // TODO: check the size of elementArray
        for (int i = 0; i < elementArray.size(); i++) {
            JsonObject element = elementArray.get(i).getAsJsonObject();

            JsonObject targetJsonObject = buildNewJsonObject(innerNestedNames, element);

            if (targetJsonObject != null) {
                targetJsonObject.addProperty(innerNestedNames[innerNestedNames.length - 1], values.get(i));
            }
        }

    }

    /**
     * @param propertyName - property name with path split by dots e.g. depth.value
     * @param value        - the value of the property with String type
     * @param jsonObject   - JsonObject which presumably contains the property
     */
    public static void overrideStringPropertyOfJsonObject(String propertyName, String value, JsonObject jsonObject) {
        String[] nestedNames = splitJsonPropertiesByDots(propertyName);

        JsonObject targetJsonObject = buildNewJsonObject(nestedNames, jsonObject);

        ofNullable(targetJsonObject)
            .ifPresent(json -> json.addProperty(nestedNames[nestedNames.length - 1], value));
    }

    private static JsonObject buildNewJsonObject(String[] nestedNames, JsonObject jsonObject) {
        JsonObject proceedJsonObject = jsonObject;
        for (int i = 0; i < nestedNames.length - 1; i++) {
            JsonElement nestedElement = proceedJsonObject.get(nestedNames[i]);
            if (nestedElement != null && nestedElement.isJsonObject()) {
                proceedJsonObject = nestedElement.getAsJsonObject();
            } else {
                return null;
            }
        }
        return proceedJsonObject;
    }

    /**
     *
     * @param name - property name with path split by dots e.g. depth.value
     * @return  a String array which is split by delimiter "."
     */
    public static String[] splitJsonPropertiesByDots(String name) {
        if(name == null) return null;
        return name.split("\\.");
    }
}