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
    private static final String PN_END = "]";

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
     * @return JsonElement list with property, if it found and null if not
     */
    public static List<JsonElement> getJsonPropertyValueFromJsonObject(String propertyName, JsonObject jsonObject) {
        List<JsonElement> result = new ArrayList<>();
        String[] propertiesHierarchy = splitJsonPropertiesByDots(propertyName);

        if (propertiesHierarchy[0].endsWith(PN_END)) {
            return getNestedJsonPropertyValueFromJsonObject(propertiesHierarchy, jsonObject);
        }

        result.add(getNestedJsonElement(propertiesHierarchy, jsonObject));
        return result;
    }

    private static List<JsonElement> getNestedJsonPropertyValueFromJsonObject(String[] propertyNestedNames, JsonObject jsonObject) {
        List<JsonElement> result = new ArrayList<>();

        String[] innerNestedNames = new String[propertyNestedNames.length - 1];
        System.arraycopy(propertyNestedNames, 1, innerNestedNames, 0, propertyNestedNames.length - 1);

        JsonArray elementArray = jsonObject.getAsJsonArray(propertyNestedNames[0].substring(0, propertyNestedNames[0].length() - 2 )) ;

        for (int i = 0; i < elementArray.size(); i++) {
            JsonObject element = elementArray.get(i).getAsJsonObject();
            JsonElement elementValue = getNestedJsonElement(innerNestedNames, element);
            result.add(elementValue);
        }
        return result;
    }

    private static JsonElement getNestedJsonElement(String[] nestedNames, JsonObject jsonObject) {
        JsonObject json = jsonObject;

        for (int i = 0; i < nestedNames.length; i++) {
            if (i == nestedNames.length - 1) {
                    return json.get(nestedNames[i]);
                } else {
                    JsonElement element = json.get(nestedNames[i]);
                    if (element == null || !element.isJsonObject()) {
                        return null;
                }
                json = element.getAsJsonObject();
            }
        }
        return json;
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

        if (nestedNames[0].endsWith(PN_END)) {
            overrideNestedNumberPropertyOfJsonObject(nestedNames, value, jsonObject);
            return;
        }

        JsonObject targetJsonObject = buildNewJsonObject(nestedNames, jsonObject);

        ofNullable(targetJsonObject)
            .ifPresent(json -> json.addProperty(nestedNames[nestedNames.length - 1], value.get(0)));
    }

    private static void overrideNestedNumberPropertyOfJsonObject(String[] nestedNames, List<Number> values, JsonObject jsonObject) {
        JsonArray elementArray = jsonObject.getAsJsonArray(nestedNames[0].substring(0, nestedNames[0].length() - 2 )) ;
        String[] innerNestedNames = new String[nestedNames.length - 1];
        System.arraycopy(nestedNames, 1, innerNestedNames, 0, nestedNames.length - 1);

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