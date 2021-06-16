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

package org.opengroup.osdu.core.common.crs;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opengroup.osdu.core.common.model.crs.ConversionRecord;
import org.opengroup.osdu.core.common.model.crs.ConvertStatus;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
public class UnitConversionTests {

    private JsonParser jsonParser = new JsonParser();
    private UnitConversionImpl unitConversion = new UnitConversionImpl();
    private JsonObject testData;

    @Before
    public void setup() {
        this.unitConversion = new UnitConversionImpl();
        this.testData = getTestData();
    }

    @Test
    public void shouldReturnOriginalRecordWhenMetaIsMissing() {
        JsonObject record = testData.getAsJsonObject("metaIsMissing");
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.unitConversion.convertUnitsToSI(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConvertStatus() == ConvertStatus.NO_FRAME_OF_REFERENCE);
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().size() == 0);
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        Assert.assertEquals(record, resultRecord);
    }

    @Test
    public void shouldReturnOriginalRecordWhenUnitIsMissingInMeta() {
        JsonObject record = testData.getAsJsonObject("unitMissingInMeta");
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.unitConversion.convertUnitsToSI(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().size() == 0);
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        Assert.assertEquals(record, resultRecord);
    }

    @Test
    public void shouldReturnOriginalRecordWhenReferenceIsMissingInMeta() {
        JsonObject record = testData.getAsJsonObject("persistableReferenceMissing");
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.unitConversion.convertUnitsToSI(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConvertStatus() == ConvertStatus.ERROR);
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().get(0).equalsIgnoreCase(UnitConversionImpl.MISSING_REFERENCE));
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        Assert.assertEquals(record, resultRecord);
    }

    @Test
    public void shouldReturnOriginalRecordWhenReferenceIsInvalidInMeta() {
        JsonObject record = testData.getAsJsonObject("persistableReferenceInvalid");
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.unitConversion.convertUnitsToSI(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConvertStatus() == ConvertStatus.ERROR);
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().get(0).equalsIgnoreCase(UnitConversionImpl.INVALID_REFERENCE));
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        Assert.assertEquals(record, resultRecord);
    }

    @Test
    public void shouldReturnOriginalRecordWhenPropertyNamesAreMissingInMeta() {
        JsonObject record = testData.getAsJsonObject("propertyNamesMissingInMeta");
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.unitConversion.convertUnitsToSI(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConvertStatus() == ConvertStatus.ERROR);
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().get(0).equalsIgnoreCase(UnitConversionImpl.MISSING_PROPERTY_NAMES));
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        Assert.assertEquals(record, resultRecord);
    }

    @Test
    public void shouldReturnOriginalRecordWhenMetaDataKindIsMissingInMeta() {
        JsonObject record = testData.getAsJsonObject("metaDataKindIsMissing");
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.unitConversion.convertUnitsToSI(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConvertStatus() == ConvertStatus.ERROR);
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().get(0).equalsIgnoreCase(UnitConversionImpl.MISSING_META_KIND));
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        Assert.assertEquals(record, resultRecord);
    }

    @Test
    public void shouldReturnOriginalRecordWhenPropertyNamesAreNotArrayInMeta() {
        JsonObject record = testData.getAsJsonObject("propertyNameAreNotArrayInMeta");
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.unitConversion.convertUnitsToSI(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConvertStatus() == ConvertStatus.ERROR);
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().get(0).equalsIgnoreCase(UnitConversionImpl.ILLEGAL_PROPERTY_NAMES));
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        Assert.assertEquals(record, resultRecord);
    }

    @Test
    public void shouldReturnOriginalRecordWhenPropertyIsMissingInData() {
        JsonObject record = testData.getAsJsonObject("propertyMissingInData");
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecord.setConvertStatus(ConvertStatus.SUCCESS);
        conversionRecords.add(conversionRecord);
        this.unitConversion.convertUnitsToSI(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConvertStatus() == ConvertStatus.SUCCESS);
        String message = String.format(UnitConversionImpl.MISSING_PROPERTY, "MD");
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().get(0).equalsIgnoreCase(message));
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        Assert.assertEquals(record, resultRecord);
    }

    @Test
    public void shouldReturnOriginalRecordWhenPropertyValueIsNullInData() {
        JsonObject record = testData.getAsJsonObject("nullPropertyValueInData");
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecord.setConvertStatus(ConvertStatus.SUCCESS);
        conversionRecords.add(conversionRecord);
        this.unitConversion.convertUnitsToSI(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConvertStatus() == ConvertStatus.SUCCESS);
        String message = String.format(UnitConversionImpl.MISSING_PROPERTY, "MD");
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().get(0).equalsIgnoreCase(message));
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        Assert.assertEquals(record, resultRecord);
    }

    @Test
    public void shouldReturnOriginalRecordWhenPropertyIsBadInData() {
        JsonObject record = testData.getAsJsonObject("badPropertyValueInData");
        JsonArray metaArray = record.getAsJsonArray("meta");
        Assert.assertEquals(1, metaArray.size());
        JsonObject meta = (JsonObject) metaArray.get(0);
        String persistableReference = meta.get("persistableReference").getAsString();
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.unitConversion.convertUnitsToSI(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConvertStatus() == ConvertStatus.ERROR);
        String message = String.format(UnitConversionImpl.ILLEGAL_PROPERTY_VALUE, "MD");
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().get(0).equalsIgnoreCase(message));
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        Assert.assertEquals(record, resultRecord);
        JsonArray resultMetaArray = resultRecord.getAsJsonArray("meta");
        Assert.assertEquals(1, resultMetaArray.size());
        JsonObject resultMeta = (JsonObject) resultMetaArray.get(0);
        String resultPersistableReference = resultMeta.get("persistableReference").getAsString();
        Assert.assertTrue(persistableReference == resultPersistableReference);
    }

    @Test
    public void shouldReturnUpdatedRecordWhenUnitMetaAndDataAreValid() {
        JsonObject record = testData.getAsJsonObject("validDataAndMeta");
        JsonArray metaArray = record.getAsJsonArray("meta");
        Assert.assertEquals(1, metaArray.size());
        JsonObject meta = (JsonObject) metaArray.get(0);
        String persistableReference = meta.get("persistableReference").getAsString();
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.unitConversion.convertUnitsToSI(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().size() == 0);
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        JsonElement data = resultRecord.get("data");
        double actualMDValue = data.getAsJsonObject().get("MD").getAsDouble();
        Assert.assertEquals(3.048, actualMDValue, 0.00001);
        JsonArray resultMetaArray = resultRecord.getAsJsonArray("meta");
        Assert.assertEquals(1, resultMetaArray.size());
        JsonObject resultMeta = (JsonObject) resultMetaArray.get(0);
        String resultPersistableReference = resultMeta.get("persistableReference").getAsString();
        Assert.assertTrue(persistableReference != resultPersistableReference);
        String resultName = resultMeta.get("name").getAsString();
        Assert.assertEquals("m", resultName);
    }

    @Test
    public void shouldReturnUpdatedRecordWhenUnitMetaAndDataAreValidAndNested() {
        JsonObject record = testData.getAsJsonObject("validNestedDataAndMeta");
        JsonArray metaArray = record.getAsJsonArray("meta");
        Assert.assertEquals(1, metaArray.size());
        JsonObject meta = (JsonObject) metaArray.get(0);
        String persistableReference = meta.get("persistableReference").getAsString();
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.unitConversion.convertUnitsToSI(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().size() == 0);
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        JsonElement data = resultRecord.get("data");
        double actualMDValue = data.getAsJsonObject().getAsJsonObject("MD").get("value").getAsDouble();
        Assert.assertEquals(3.048, actualMDValue, 0.00001);
        JsonArray resultMetaArray = resultRecord.getAsJsonArray("meta");
        Assert.assertEquals(1, resultMetaArray.size());
        JsonObject resultMeta = (JsonObject) resultMetaArray.get(0);
        String resultPersistableReference = resultMeta.get("persistableReference").getAsString();
        Assert.assertTrue(persistableReference != resultPersistableReference);
        String resultName = resultMeta.get("name").getAsString();
        Assert.assertEquals("m", resultName);
    }

    @Test
    public void shouldReturnOriginalRecordWhenPropertyValueIsNullInDataAndNested() {
        JsonObject record = testData.getAsJsonObject("nullNestedPropertyValueInData");
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecord.setConvertStatus(ConvertStatus.SUCCESS);
        conversionRecords.add(conversionRecord);
        this.unitConversion.convertUnitsToSI(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConvertStatus() == ConvertStatus.SUCCESS);
        String message = String.format(UnitConversionImpl.MISSING_PROPERTY, "MD.value");
        Assert.assertEquals(message, conversionRecords.get(0).getConversionMessages().get(0));
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        Assert.assertEquals(record, resultRecord);
    }

    @Test
    public void shouldReturnUpdatedRecordWhenPersistableReferenceIsJsonObject() {
        JsonObject record = testData.getAsJsonObject("persistableAsJsonObject");
        JsonArray metaArray = record.getAsJsonArray("meta");
        Assert.assertEquals(1, metaArray.size());
        JsonObject meta = (JsonObject) metaArray.get(0);
        String persistableReference = meta.get("persistableReference").toString();
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.unitConversion.convertUnitsToSI(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().size() == 0);
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        JsonElement data = resultRecord.get("data");
        double actualMDValue = data.getAsJsonObject().get("MD").getAsDouble();
        Assert.assertEquals(3.048, actualMDValue, 0.00001);
        JsonArray resultMetaArray = resultRecord.getAsJsonArray("meta");
        Assert.assertEquals(1, resultMetaArray.size());
        JsonObject resultMeta = (JsonObject) resultMetaArray.get(0);
        String resultPersistableReference = resultMeta.get("persistableReference").getAsString();
        Assert.assertTrue(persistableReference != resultPersistableReference);
        String resultName = resultMeta.get("name").getAsString();
        Assert.assertEquals("m/s", resultName);
    }

    @Test
    public void shouldReturnUpdatedRecordWhenDataContainsDepthWitUnitKey() {
        JsonObject record = testData.getAsJsonObject("dataContainsDepthUnitKey");
        JsonArray metaArray = record.getAsJsonArray("meta");
        Assert.assertEquals(1, metaArray.size());
        JsonObject meta = (JsonObject)metaArray.get(0);
        String persistableReference = meta.get("persistableReference").getAsString();
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.unitConversion.convertUnitsToSI(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().size() == 0);
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        JsonElement data = resultRecord.get("data");
        double actualDepthValue = data.getAsJsonObject().get("depth").getAsJsonObject().get("inner")
                .getAsJsonObject().get("value").getAsDouble();
        String actualUnitKeyValue = data.getAsJsonObject().get("depth").getAsJsonObject().get("inner").getAsJsonObject()
                .get("unitKey").getAsString();
        Assert.assertEquals(3.048, actualDepthValue, 0.00001);
        Assert.assertEquals("m", actualUnitKeyValue);
        JsonArray resultMetaArray = resultRecord.getAsJsonArray("meta");
        Assert.assertEquals(1, resultMetaArray.size());
        JsonObject resultMeta = (JsonObject)resultMetaArray.get(0);
        String resultPersistableReference = resultMeta.get("persistableReference").getAsString();
        Assert.assertTrue(persistableReference != resultPersistableReference);
        String resultName = resultMeta.get("name").getAsString();
        Assert.assertEquals("m", resultName);
    }

    @Test
    public void shouldReturnUpdatedRecordWhenDataContainsNestedArrayProperties() {
        JsonObject record = testData.getAsJsonObject("validNestedArray");
        JsonArray metaArray = record.getAsJsonArray("meta");
        Assert.assertEquals(1, metaArray.size());
        JsonObject meta = (JsonObject) metaArray.get(0);
        String persistableReference = meta.get("persistableReference").getAsString();
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.unitConversion.convertUnitsToSI(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().size() == 0);
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        JsonElement data = resultRecord.get("data");
        JsonArray markers = data.getAsJsonObject().getAsJsonArray("markers");
        JsonObject item1 = markers.get(0).getAsJsonObject();
        double actualConvertedMeasuredDepthValue1 = item1.get("measuredDepth").getAsDouble();
        Assert.assertEquals(3.048, actualConvertedMeasuredDepthValue1, 0.00001);
        JsonObject item2 = markers.get(1).getAsJsonObject();
        double actualConvertedMeasuredDepthValue2 = item2.get("measuredDepth").getAsDouble();
        Assert.assertEquals(6.096, actualConvertedMeasuredDepthValue2, 0.00001);
        JsonArray resultMetaArray = resultRecord.getAsJsonArray("meta");
        Assert.assertEquals(1, resultMetaArray.size());
        JsonObject resultMeta = (JsonObject) resultMetaArray.get(0);
        String resultPersistableReference = resultMeta.get("persistableReference").getAsString();
        Assert.assertTrue(persistableReference != resultPersistableReference);
        String resultName = resultMeta.get("name").getAsString();
        Assert.assertEquals("m", resultName);
    }

    @Test
    public void shouldReturnOriginalRecordWhenNestedArrayPropertyValueTypeIsInvalidInDataAndNested() {
        JsonObject record = testData.getAsJsonObject("nestedArrayWithInvalidValueType");
        JsonArray metaArray = record.getAsJsonArray("meta");
        Assert.assertEquals(1, metaArray.size());
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecord.setConvertStatus(ConvertStatus.SUCCESS);
        conversionRecords.add(conversionRecord);
        this.unitConversion.convertUnitsToSI(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConvertStatus() == ConvertStatus.ERROR);
        String message = String.format(UnitConversionImpl.ILLEGAL_PROPERTY_VALUE, "markers[1].measuredDepth");
        Assert.assertEquals(message, conversionRecords.get(0).getConversionMessages().get(0));
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        Assert.assertEquals(record, resultRecord);
    }

    @Test
    public void shouldReturnOriginalRecordWhenIOneOfNestedArrayPropertyValueIsMissingInDataAndNested() {
        JsonObject record = testData.getAsJsonObject("nestedArrayWithMissingValue");
        JsonArray metaArray = record.getAsJsonArray("meta");
        Assert.assertEquals(1, metaArray.size());
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecord.setConvertStatus(ConvertStatus.SUCCESS);
        conversionRecords.add(conversionRecord);
        this.unitConversion.convertUnitsToSI(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConvertStatus() == ConvertStatus.SUCCESS);
        String message = String.format(UnitConversionImpl.MISSING_PROPERTY, "markers[1].measuredDepth");
        Assert.assertEquals(message, conversionRecords.get(0).getConversionMessages().get(0));
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        Assert.assertEquals(record, resultRecord);
    }

    private JsonObject getTestData() {
        InputStream inStream = this.getClass().getResourceAsStream("/testdata/nested-data.json");
        BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(br);
        JsonObject result = gson.fromJson(reader, JsonObject.class);
        return result;
    }
}