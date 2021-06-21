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

package org.opengroup.osdu.core.common.model.units;

import com.google.gson.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opengroup.osdu.core.common.util.JsonUtils;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.opengroup.osdu.core.common.util.JsonUtils.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JsonObject.class, JsonArray.class, JsonPrimitive.class, JsonNull.class})
public class JsonUtilsTest {

    private static final String JSON_AS_STRING_VALUE = "test json as string value";
    private static final String JSON_TO_STRING_VALUE = "test json to string value";

    private JsonObject mockJsonObject;
    private JsonArray mockJsonArray;
    private JsonPrimitive mockJsonPrimitive;
    private JsonNull mockJsonNull;

    @Before
    public void before() {
        mockJsonObject = mock(JsonObject.class);
        mockJsonArray = mock(JsonArray.class);
        mockJsonPrimitive = mock(JsonPrimitive.class);
        mockJsonNull = mock(JsonNull.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void jsonElementToString_shouldThrowException_onNullInputArg() {
        JsonUtils.jsonElementToString(null);
    }

    @Test
    public void jsonElementToString_shouldReturnString_forJsonObject() {
        setupJsonObjectMock();

        String actualValue = JsonUtils.jsonElementToString(mockJsonObject);

        assertEquals(JSON_TO_STRING_VALUE, actualValue);
        verify(mockJsonObject, never()).getAsString();
    }

    @Test
    public void jsonElementToString_shouldReturnString_forJsonArray_withOneElement() {
        setupJsonArrayMock(1);

        String actualValue = JsonUtils.jsonElementToString(mockJsonArray);

        assertEquals(JSON_AS_STRING_VALUE, actualValue);
        verify(mockJsonArray, times(1)).getAsString();
    }

    @Test
    public void jsonElementToString_shouldReturnString_forJsonArray_withMoreThanOneElement() {
        setupJsonArrayMock(2);

        String actualValue = JsonUtils.jsonElementToString(mockJsonArray);

        assertEquals(JSON_TO_STRING_VALUE, actualValue);
        verify(mockJsonArray, never()).getAsString();
    }

    @Test
    public void jsonElementToString_shouldReturnString_forJsonPrimitive() {
        setupJsonPrimitiveMock();

        String actualValue = JsonUtils.jsonElementToString(mockJsonPrimitive);

        assertEquals(JSON_AS_STRING_VALUE, actualValue);
        verify(mockJsonPrimitive, times(1)).getAsString();
    }

    @Test
    public void jsonElementToString_shouldReturnString_forJsonNull() {
        setupJsonNullMock();

        String actualValue = JsonUtils.jsonElementToString(mockJsonNull);

        assertEquals(JSON_TO_STRING_VALUE, actualValue);
        verify(mockJsonNull, never()).getAsString();
    }

    // ---- JsonUtils.getJsonPropertyValueFromJsonObject tests ----

    @Test
    public void getJsonPropertyValueFromJsonObjectProperty_shouldReturnProperty_whenItPresented() {
        String propertyName = "depth.value";
        JsonObject internalJsonObject = mock(JsonObject.class);

        setupMocksForJsonPropertyTests(internalJsonObject);
        when(internalJsonObject.get("value")).thenReturn(mockJsonPrimitive);

        List<JsonElement> jsonElement = getJsonPropertyValueFromJsonObject(propertyName, mockJsonObject);
        assertSame(mockJsonPrimitive, jsonElement.get(0));

        verify(mockJsonObject, times(1)).get("depth");
        verify(internalJsonObject, times(1)).get("value");
    }

    @Test
    public void getJsonPropertyValueFromJsonObject_shouldReturnNull_whenPropertyNotPresented() {
        String propertyName = "depth.value";
        JsonObject internalJsonObject = mock(JsonObject.class);

        setupMocksForJsonPropertyTests(internalJsonObject);
        when(internalJsonObject.get("value")).thenReturn(null);

        assertNull(getJsonPropertyValueFromJsonObject(propertyName, mockJsonObject).get(0));

        verify(mockJsonObject, times(1)).get("depth");
        verify(internalJsonObject, times(1)).get("value");
    }

    @Test
    public void getJsonPropertyValueFromJsonObject_shouldReturnNull_whenJsonObjectHierarchyNotMatchWithPropertyNamePath() {
        String propertyName = "depth.deeper.value";
        JsonObject internalJsonObject = mock(JsonObject.class);

        setupMocksForJsonPropertyTests(internalJsonObject);

        assertNull(getJsonPropertyValueFromJsonObject(propertyName, mockJsonObject).get(0));

        verify(mockJsonObject, times(1)).get("depth");
        verify(internalJsonObject, times(1)).get("deeper");
    }

    @Test
    public void getJsonPropertyValueFromJsonObject_shouldReturnListOfProperty_whenNestedArrayItemsPropertyPresented() {
        String propertyName = "markers[].value";

        setupJsonArrayMock(3);
        when(mockJsonObject.getAsJsonArray("markers")).thenReturn(mockJsonArray);
        when(mockJsonObject.get("value")).thenReturn(mockJsonPrimitive);

        List<JsonElement> result = getJsonPropertyValueFromJsonObject(propertyName, mockJsonObject);

        assertEquals(3, result.size());
        verify(mockJsonObject, times(3)).get("value");
        assertSame(mockJsonPrimitive, result.get(0));
        assertSame(mockJsonPrimitive, result.get(1));
        assertSame(mockJsonPrimitive, result.get(2));
    }

    @Test
    public void getJsonPropertyValueFromJsonObject_shouldReturnListOfNull_whenNestedArrayItemsPropertyNotPresented() {
        String propertyName = "markers[].value";

        setupJsonArrayMock(3);
        when(mockJsonObject.getAsJsonArray("markers")).thenReturn(mockJsonArray);

        List<JsonElement> result = getJsonPropertyValueFromJsonObject(propertyName, mockJsonObject);

        assertEquals(3, result.size());
        verify(mockJsonObject, times(3)).get("value");
        assertNull(result.get(0));
        assertNull(result.get(1));
        assertNull(result.get(2));
    }

    // ---- JsonUtils.isJsonPropertyPresentedInJsonObject tests ----

    @Test
    public void isJsonPropertyPresentedInJsonObject_shouldReturnTrue_IfPropertyFound() {
        String propertyName = "depth.value";
        JsonObject internalJsonObject = mock(JsonObject.class);

        setupMocksForJsonPropertyTests(internalJsonObject);
        when(internalJsonObject.get("value")).thenReturn(mockJsonPrimitive);

        assertTrue(isJsonPropertyPresentedInJsonObject(propertyName, mockJsonObject));

        verify(mockJsonObject, times(1)).get("depth");
        verify(internalJsonObject, times(1)).get("value");
    }

    @Test
    public void isJsonPropertyPresentedInJsonObject_shouldReturnFalse_IfPropertyNotFound() {
        String propertyName = "depth.value";
        JsonObject internalJsonObject = mock(JsonObject.class);

        setupMocksForJsonPropertyTests(internalJsonObject);
        when(internalJsonObject.get("value")).thenReturn(null);

        assertFalse(isJsonPropertyPresentedInJsonObject(propertyName, mockJsonObject));

        verify(mockJsonObject, times(1)).get("depth");
        verify(internalJsonObject, times(1)).get("value");
    }

    @Test
    public void isJsonPropertyPresentedInJsonObject_shouldReturnFalse_whenJsonObjectHierarchyNotMatchWithPropertyNamePath() {
        String propertyName = "depth.deeper.value";
        JsonObject internalJsonObject = mock(JsonObject.class);

        setupMocksForJsonPropertyTests(internalJsonObject);

        assertFalse(isJsonPropertyPresentedInJsonObject(propertyName, mockJsonObject));

        verify(mockJsonObject, times(1)).get("depth");
        verify(internalJsonObject, times(1)).get("deeper");
    }

    // ---- JsonUtils.overrideNumberPropertyOfJsonObject tests ----

    @Test
    public void overrideNumberPropertyOfJsonObject_succeed_whenJsonObjectPresentedInPath() {
        String propertyName = "depth.value";
        JsonObject internalJsonObject = mock(JsonObject.class);
        Integer value = 42;
        List<Number> values = new ArrayList<>();
        values.add(value);

        setupMocksForJsonPropertyTests(internalJsonObject);

        overrideNumberPropertyOfJsonObject(propertyName, values, mockJsonObject);

        verify(mockJsonObject, times(1)).get("depth");
        verify(internalJsonObject, times(1)).addProperty("value", value);
    }

    @Test
    public void overrideNumberPropertyOfJsonObject_notHappened_whenJsonObjectContainsNonJsonObjectElement() {
        String propertyName = "depth.value";
        JsonObject internalJsonObject = mock(JsonObject.class);
        Integer value = 42;
        List<Number> values = new ArrayList<>();
        values.add(value);

        when(mockJsonObject.get("depth")).thenReturn(mockJsonPrimitive);
        when(mockJsonPrimitive.isJsonObject()).thenReturn(false);

        overrideNumberPropertyOfJsonObject(propertyName, values, mockJsonObject);

        verify(mockJsonObject, times(1)).get("depth");
        verify(internalJsonObject, never()).addProperty(anyString(), any(Number.class));
        verify(mockJsonObject, never()).addProperty(anyString(), any(Number.class));
    }

    @Test
    public void overrideNumberPropertyOfJsonObject_succeed_whenJsonObjectPresentedInNestedArray() {
        String propertyName = "markers[].value";
        Integer value1 = 12;
        Integer value2 = 22;
        Integer value3 = 32;
        List<Number> values = new ArrayList<>();
        values.add(value1);
        values.add(value2);
        values.add(value3);


        setupJsonArrayMock(3);
        when(mockJsonObject.getAsJsonArray("markers")).thenReturn(mockJsonArray);

        overrideNumberPropertyOfJsonObject(propertyName, values, mockJsonObject);
        verify(mockJsonObject, times(1)).addProperty("value", 12);
        verify(mockJsonObject, times(1)).addProperty("value", 22);
        verify(mockJsonObject, times(1)).addProperty("value", 32);
    }

    // ---- JsonUtils.overrideStringPropertyOfJsonObject tests ----

    @Test
    public void overrideStringPropertyOfJsonObject_succeed_whenJsonObjectPresentedInPath() {
        String propertyName = "depth.value";
        JsonObject internalJsonObject = mock(JsonObject.class);
        String value = "new value";

        setupMocksForJsonPropertyTests(internalJsonObject);

        overrideStringPropertyOfJsonObject(propertyName, value, mockJsonObject);

        verify(mockJsonObject, times(1)).get("depth");
        verify(internalJsonObject, times(1)).addProperty("value", value);
    }

    @Test
    public void overrideStringPropertyOfJsonObject_notHappened_whenJsonObjectContainsNonJsonObjectElement() {
        String propertyName = "depth.value";
        JsonObject internalJsonObject = mock(JsonObject.class);
        String value = "new value";

        when(mockJsonObject.get("depth")).thenReturn(mockJsonPrimitive);
        when(mockJsonPrimitive.isJsonObject()).thenReturn(false);

        overrideStringPropertyOfJsonObject(propertyName, value, mockJsonObject);

        verify(mockJsonObject, times(1)).get("depth");
        verify(internalJsonObject, never()).addProperty(anyString(), any(Number.class));
        verify(mockJsonObject, never()).addProperty(anyString(), any(Number.class));
    }

    // ---- JsonUtils.splitJsonPropertiesByDots tests ----

    @Test
    public void splitJsonPropertiesByDots_shouldSplitString_ForPropertyWithMultipleElements() {
        String propertyName = "depth.value";
        String[] expectedArray = {"depth", "value"};

        String[] actualArray = splitJsonPropertiesByDots(propertyName);

        assertArrayEquals(expectedArray, actualArray);
    }

    @Test
    public void splitJsonPropertiesByDots_shouldSplitString_ForPropertyWithSingleElement() {
        String propertyName = "depth#value";
        String[] expectedArray = {"depth#value"};

        String[] actualArray = splitJsonPropertiesByDots(propertyName);

        assertArrayEquals(expectedArray, actualArray);
    }

    @Test
    public void splitJsonPropertiesByDots_shouldReturnNull_ForNullInputParam() {
        assertNull(splitJsonPropertiesByDots(null));
    }

    private void setupJsonObjectMock() {
        when(mockJsonObject.toString()).thenReturn(JSON_TO_STRING_VALUE);
    }

    private void setupJsonArrayMock(int size) {
        when(mockJsonArray.isJsonArray()).thenReturn(true);
        when(mockJsonArray.size()).thenReturn(size);
        when(mockJsonArray.getAsString()).thenReturn(JSON_AS_STRING_VALUE);
        when(mockJsonArray.toString()).thenReturn(JSON_TO_STRING_VALUE);
        when(mockJsonArray.get(anyInt())).thenReturn(mockJsonObject);
        when(mockJsonObject.getAsJsonObject()).thenReturn(mockJsonObject);
    }

    private void setupJsonPrimitiveMock() {
        when(mockJsonPrimitive.isJsonPrimitive()).thenReturn(true);
        when(mockJsonPrimitive.getAsString()).thenReturn(JSON_AS_STRING_VALUE);
    }

    private void setupJsonNullMock() {
        when(mockJsonNull.toString()).thenReturn(JSON_TO_STRING_VALUE);
    }

    private void setupMocksForJsonPropertyTests(JsonObject internalJsonObject) {
        when(mockJsonObject.get("depth")).thenReturn(internalJsonObject);
        when(internalJsonObject.getAsJsonObject()).thenReturn(internalJsonObject);
        when(internalJsonObject.isJsonObject()).thenReturn(true);
    }
}