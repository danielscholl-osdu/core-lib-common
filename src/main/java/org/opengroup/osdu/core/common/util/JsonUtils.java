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
        String[] propertiesHierarchy = splitJsonPropertiesByDots(propertyName);

        if (propertiesHierarchy[0].endsWith(PN_END) && isNestedArrayElementHomogeneous(propertiesHierarchy[0])) {
            return getNestedJsonPropertyValueFromJsonObject(propertiesHierarchy, jsonObject);
        } else if (propertiesHierarchy[0].endsWith(PN_END)) {
            return getOneNestedJsonProperyValueFromJsonObject(propertiesHierarchy, jsonObject);
        }

        List<JsonElement> result = new ArrayList<>();
        result.add(getNestedJsonElement(propertiesHierarchy, jsonObject));
        return result;
    }

    private static List<JsonElement> getOneNestedJsonProperyValueFromJsonObject(String[] propertyNestedNames, JsonObject jsonObject) {
        List<JsonElement> result = new ArrayList<>();
        String[] innerNestedNames = getInnerNestedPropertyNames(propertyNestedNames);
        JsonArray elementArray = jsonObject.getAsJsonArray(getNestedJsonArrayName(propertyNestedNames[0])) ;
        int elementIndex = getNestedArrayElementIndex(propertyNestedNames[0]);

        if (elementIndex < 0 || elementIndex >= elementArray.size()) {
            result.add(null);
        } else {
            JsonElement element = getNestedJsonElement(innerNestedNames, elementArray.get(elementIndex).getAsJsonObject());
            result.add(element);
        }

        return result;
    }

    private static List<JsonElement> getNestedJsonPropertyValueFromJsonObject(String[] propertyNestedNames, JsonObject jsonObject) {
        List<JsonElement> result = new ArrayList<>();

        String[] innerNestedNames = getInnerNestedPropertyNames(propertyNestedNames);
        JsonArray elementArray = jsonObject.getAsJsonArray(getNestedJsonArrayName(propertyNestedNames[0])) ;

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

        if (nestedNames[0].endsWith(PN_END) && isNestedArrayElementHomogeneous(nestedNames[0])) {
            overrideNestedNumberPropertyOfJsonObject(nestedNames, value, jsonObject);
            return;
        } else if (nestedNames[0].endsWith(PN_END)) {
            overrideOneNestedNumberPorpertyOfJsonObject(nestedNames, value, jsonObject);
            return;
        }

        JsonObject targetJsonObject = buildNewJsonObject(nestedNames, jsonObject);

        ofNullable(targetJsonObject)
            .ifPresent(json -> json.addProperty(nestedNames[nestedNames.length - 1], value.get(0)));
    }

    public static void overrideNestedStringPropertyOfJsonObject(String propertyName, List<String> value, JsonObject jsonObject) {
        String[] nestedNames = splitJsonPropertiesByDots(propertyName);

        if (nestedNames[0].endsWith(PN_END) && isNestedArrayElementHomogeneous(nestedNames[0])) {
            overrideNestedStringPropertyOfJsonObject(nestedNames, value, jsonObject);
            return;
        } else if (nestedNames[0].endsWith(PN_END)) {
            overrideOneNestedStringPorpertyOfJsonObject(nestedNames, value, jsonObject);
            return;
        }

        JsonObject targetJsonObject = buildNewJsonObject(nestedNames, jsonObject);

        ofNullable(targetJsonObject)
                .ifPresent(json -> json.addProperty(nestedNames[nestedNames.length - 1], value.get(0)));
    }

    private static void overrideOneNestedNumberPorpertyOfJsonObject(String[] nestedNames, List<Number> values, JsonObject jsonObject) {
        String[] innerNestedNames = getInnerNestedPropertyNames(nestedNames);
        JsonArray elementArray = jsonObject.getAsJsonArray(getNestedJsonArrayName(nestedNames[0])) ;
        int elementIndex = getNestedArrayElementIndex(nestedNames[0]);

        if (elementIndex < 0 || elementIndex >= elementArray.size()) {
            return;
        }

        JsonObject element = elementArray.get(elementIndex).getAsJsonObject();
        JsonObject targetJsonObject = buildNewJsonObject(innerNestedNames, element);

        if (targetJsonObject != null) {
            targetJsonObject.addProperty(innerNestedNames[innerNestedNames.length - 1], values.get(0));
        }
    }

    private static void overrideOneNestedStringPorpertyOfJsonObject(String[] nestedNames, List<String> values, JsonObject jsonObject) {
        String[] innerNestedNames = getInnerNestedPropertyNames(nestedNames);
        JsonArray elementArray = jsonObject.getAsJsonArray(getNestedJsonArrayName(nestedNames[0])) ;
        int elementIndex = getNestedArrayElementIndex(nestedNames[0]);

        if (elementIndex < 0 || elementIndex >= elementArray.size()) {
            return;
        }

        JsonObject element = elementArray.get(elementIndex).getAsJsonObject();
        JsonObject targetJsonObject = buildNewJsonObject(innerNestedNames, element);

        if (targetJsonObject != null) {
            targetJsonObject.addProperty(innerNestedNames[innerNestedNames.length - 1], values.get(0));
        }
    }

    private static void overrideNestedNumberPropertyOfJsonObject(String[] nestedNames, List<Number> values, JsonObject jsonObject) {
        JsonArray elementArray = jsonObject.getAsJsonArray(getNestedJsonArrayName(nestedNames[0])) ;
        String[] innerNestedNames = getInnerNestedPropertyNames(nestedNames);

        for (int i = 0; i < elementArray.size(); i++) {
            JsonObject element = elementArray.get(i).getAsJsonObject();

            JsonObject targetJsonObject = buildNewJsonObject(innerNestedNames, element);

            if (targetJsonObject != null) {
                targetJsonObject.addProperty(innerNestedNames[innerNestedNames.length - 1], values.get(i));
            }
        }

    }

    private static void overrideNestedStringPropertyOfJsonObject(String[] nestedNames, List<String> values, JsonObject jsonObject) {
        JsonArray elementArray = jsonObject.getAsJsonArray(getNestedJsonArrayName(nestedNames[0])) ;
        String[] innerNestedNames = getInnerNestedPropertyNames(nestedNames);

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

    public static boolean isNestedArrayElementHomogeneous(String nestedArrayName) {
        int openIndex = nestedArrayName.indexOf("[");
        return openIndex == nestedArrayName.length() - 2;
    }

    public static String[] getInnerNestedPropertyNames(String[] nestedPropertyNames) {
        String[] innerNestedNames = new String[nestedPropertyNames.length - 1];
        System.arraycopy(nestedPropertyNames, 1, innerNestedNames, 0, nestedPropertyNames.length - 1);

        return innerNestedNames;
    }

    public static String getNestedJsonArrayName(String nestedArrayName) {
        int openIndex = nestedArrayName.indexOf("[");
        return nestedArrayName.substring(0, openIndex);
    }

    public static int getNestedArrayElementIndex(String itemNameWithIndex) {
        int openIndex = itemNameWithIndex.indexOf("[") + 1;
        int closeIndex = itemNameWithIndex.indexOf("]");
        String indexString = itemNameWithIndex.substring(openIndex, closeIndex);

        int index;
        try {
            index = Integer.parseInt(indexString);
        } catch (Exception e) {
            index = -1;
        }

        return index;
    }
}