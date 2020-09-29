package org.opengroup.osdu.core.common.model.units;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opengroup.osdu.core.common.util.JsonUtils;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

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

    private void setupJsonObjectMock() {
        when(mockJsonObject.toString()).thenReturn(JSON_TO_STRING_VALUE);
    }

    private void setupJsonArrayMock(int size) {
        when(mockJsonArray.isJsonArray()).thenReturn(true);
        when(mockJsonArray.size()).thenReturn(size);
        when(mockJsonArray.getAsString()).thenReturn(JSON_AS_STRING_VALUE);
        when(mockJsonArray.toString()).thenReturn(JSON_TO_STRING_VALUE);
    }

    private void setupJsonPrimitiveMock() {
        when(mockJsonPrimitive.isJsonPrimitive()).thenReturn(true);
        when(mockJsonPrimitive.getAsString()).thenReturn(JSON_AS_STRING_VALUE);
    }

    private void setupJsonNullMock() {
        when(mockJsonNull.toString()).thenReturn(JSON_TO_STRING_VALUE);
    }
}