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

package org.opengroup.osdu.core.common.crs.dates;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opengroup.osdu.core.common.model.crs.ConversionRecord;
import org.opengroup.osdu.core.common.model.crs.ConvertStatus;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
public class DatesConversionTests {

    private JsonParser jsonParser = new JsonParser();
    private DatesConversionImpl datesConversion = new DatesConversionImpl();

    private static final String BASE_DATETIME_DAT = "{\"format\":\"yyyy-MM-dd\",\"type\":\"DAT\"}";
    private static final String BASE_DATETIME_DTM = "{\"format\":\"yyyy-MM-ddTHH:mm:ss.fffZ\",\"timeZone\":\"UTC\",\"type\":\"DTM\"}";

    @Before
    public void setup() throws Exception {
        this.datesConversion = new DatesConversionImpl();
    }

    @Test
    public void shouldReturnOriginalRecordWhenDataBlockIsMissing() {
        String stringRecord = "{\"id\": \"unit-test-1\",\"kind\": \"unit:test:1.0.0\",\"acl\": {\"viewers\": [\"viewers@unittest.com\"],\"owners\": [\"owners@unittest.com\"]},\"legal\": {\"legaltags\": [\"unit-test-legal\"],\"otherRelevantDataCountries\": [\"US\"]},\"meta\": [{\"path\": \"\",\"kind\": \"DateTime\",\"persistableReference\": \"{\\\"type\\\": \\\"DAT\\\",\\\"format\\\": \\\"yyyy-MM-dd\\\"}\",\"propertyNames\": [\"creationDate\"],\"name\": \"GCS_WGS_1984\"}]}";
        JsonObject record = (JsonObject) this.jsonParser.parse(stringRecord);
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.datesConversion.convertDatesToISO(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConvertStatus() == ConvertStatus.ERROR);
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().get(0).equalsIgnoreCase(DatesConversionServiceErrorMessages.MISSING_DATA_BLOCK));
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        Assert.assertEquals(record, resultRecord);
    }

    @Test
    public void shouldReturnOriginalRecordWhenMetaIsMissing() {
        String stringRecord = "{\"id\": \"unit-test-1\",\"kind\": \"unit:test:1.0.0\",\"acl\": {\"viewers\": [\"viewers@unittest.com\"],\"owners\": [\"owners@unittest.com\"]},\"legal\": {\"legaltags\": [\"unit-test-legal\"],\"otherRelevantDataCountries\": [\"US\"]},\"data\": {\"msg\": \"test record\",\"creationDate\": \"2019-08-03\"}}";
        JsonObject record = (JsonObject) this.jsonParser.parse(stringRecord);
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.datesConversion.convertDatesToISO(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConvertStatus() == ConvertStatus.NO_FRAME_OF_REFERENCE);
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().size() == 0);
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        Assert.assertEquals(record, resultRecord);
    }

    @Test
    public void shouldReturnOriginalRecordWhenKindIsMissingInMeta() {
        String stringRecord = "{\"id\": \"unit-test-1\",\"kind\": \"unit:test:1.0.0\",\"acl\": {\"viewers\": [\"viewers@unittest.com\"],\"owners\": [\"owners@unittest.com\"]},\"legal\": {\"legaltags\": [\"unit-test-legal\"],\"otherRelevantDataCountries\": [\"US\"]},\"data\": {\"msg\": \"test record\",\"creationDate\": \"2019-08-03\"},\"meta\": [{\"path\": \"\",\"persistableReference\": \"{\\\"type\\\": \\\"DAT\\\",\\\"format\\\": \\\"yyyy-MM-dd\\\"}\",\"propertyNames\": [\"creationDate\"],\"name\": \"GCS_WGS_1984\"}]}";
        JsonObject record = (JsonObject) this.jsonParser.parse(stringRecord);
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.datesConversion.convertDatesToISO(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConvertStatus() == ConvertStatus.ERROR);
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().get(0).equalsIgnoreCase(DatesConversionServiceErrorMessages.MISSING_META_KIND));
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        Assert.assertEquals(record, resultRecord);
    }

    @Test
    public void shouldReturnOriginalRecordWhenPropertyNamesAreMissingInMeta() {
        String stringRecord = "{\"id\": \"unit-test-1\",\"kind\": \"unit:test:1.0.0\",\"acl\": {\"viewers\": [\"viewers@unittest.com\"],\"owners\": [\"owners@unittest.com\"]},\"legal\": {\"legaltags\": [\"unit-test-legal\"],\"otherRelevantDataCountries\": [\"US\"]},\"data\": {\"msg\": \"test record\",\"creationDate\": \"2019-08-03\"},\"meta\": [{\"path\": \"\",\"kind\": \"DateTime\",\"persistableReference\": \"{\\\"type\\\": \\\"DAT\\\",\\\"format\\\": \\\"yyyy-MM-dd\\\"}\",\"name\": \"GCS_WGS_1984\"}]}";
        JsonObject record = (JsonObject) this.jsonParser.parse(stringRecord);
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.datesConversion.convertDatesToISO(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConvertStatus() == ConvertStatus.ERROR);
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().get(0).equalsIgnoreCase(DatesConversionServiceErrorMessages.MISSING_PROPERTY_NAMES));
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        Assert.assertEquals(record, resultRecord);
    }

    @Test
    public void shouldReturnOriginalRecordWhenPropertyNamesAreNotArrayInMeta() {
        String stringRecord = "{\"id\": \"unit-test-1\",\"kind\": \"unit:test:1.0.0\",\"acl\": {\"viewers\": [\"viewers@unittest.com\"],\"owners\": [\"owners@unittest.com\"]},\"legal\": {\"legaltags\": [\"unit-test-legal\"],\"otherRelevantDataCountries\": [\"US\"]},\"data\": {\"msg\": \"test record\",\"creationDate\": \"2019-08-03\"},\"meta\": [{\"path\": \"\",\"kind\": \"DateTime\",\"persistableReference\": \"{\\\"type\\\": \\\"DAT\\\",\\\"format\\\": \\\"yyyy-MM-dd\\\"}\",\"propertyNames\": \"creationDate\",\"name\": \"GCS_WGS_1984\"}]}";
        JsonObject record = (JsonObject) this.jsonParser.parse(stringRecord);
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.datesConversion.convertDatesToISO(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConvertStatus() == ConvertStatus.ERROR);
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().get(0).equalsIgnoreCase(DatesConversionServiceErrorMessages.ILLEGAL_PROPERTY_NAMES));
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        Assert.assertEquals(record, resultRecord);
    }

    @Test
    public void shouldReturnOriginalRecordWhenReferenceIsInvalidInMeta() {
        String stringRecord = "{\"id\": \"unit-test-1\",\"kind\": \"unit:test:1.0.0\",\"acl\": {\"viewers\": [\"viewers@unittest.com\"],\"owners\": [\"owners@unittest.com\"]},\"legal\": {\"legaltags\": [\"unit-test-legal\"],\"otherRelevantDataCountries\": [\"US\"]},\"data\": {\"msg\": \"test record\",\"creationDate\": \"2019-08-03\"},\"meta\": [{\"path\": \"\",\"kind\": \"DateTime\",\"persistableReference\": \"Reference\",\"propertyNames\": [\"creationDate\"],\"name\": \"GCS_WGS_1984\"}]}";
        JsonObject record = (JsonObject) this.jsonParser.parse(stringRecord);
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.datesConversion.convertDatesToISO(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConvertStatus() == ConvertStatus.ERROR);
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().get(0).equalsIgnoreCase(DatesConversionServiceErrorMessages.INVALID_REFERENCE));
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        Assert.assertEquals(record, resultRecord);
    }

    @Test
    public void shouldReturnOriginalRecordWhenReferenceIsMissingInMeta() {
        String stringRecord = "{\"id\": \"unit-test-1\",\"kind\": \"unit:test:1.0.0\",\"acl\": {\"viewers\": [\"viewers@unittest.com\"],\"owners\": [\"owners@unittest.com\"]},\"legal\": {\"legaltags\": [\"unit-test-legal\"],\"otherRelevantDataCountries\": [\"US\"]},\"data\": {\"msg\": \"test record\",\"creationDate\": \"2019-08-03\"},\"meta\": [{\"path\": \"\",\"kind\": \"DateTime\",\"propertyNames\": [\"creationDate\"],\"name\": \"GCS_WGS_1984\"}]}";
        JsonObject record = (JsonObject) this.jsonParser.parse(stringRecord);
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.datesConversion.convertDatesToISO(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConvertStatus() == ConvertStatus.ERROR);
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().get(0).equalsIgnoreCase(DatesConversionServiceErrorMessages.MISSING_REFERENCE));
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        Assert.assertEquals(record, resultRecord);
    }

    @Test
    public void shouldReturnOriginalRecordWhenPropertyIsMissingInData() {
        String stringRecord = "{\"id\": \"unit-test-1\",\"kind\": \"unit:test:1.0.0\",\"acl\": {\"viewers\": [\"viewers@unittest.com\"],\"owners\": [\"owners@unittest.com\"]},\"legal\": {\"legaltags\": [\"unit-test-legal\"],\"otherRelevantDataCountries\": [\"US\"]},\"data\": {\"msg\": \"test record\",\"modifyDate\": \"2019-08-03\"},\"meta\": [{\"path\": \"\",\"kind\": \"DateTime\",\"persistableReference\": \"{\\\"type\\\": \\\"DAT\\\",\\\"format\\\": \\\"yyyy-MM-dd\\\"}\",\"propertyNames\": [\"creationDate\"],\"name\": \"GCS_WGS_1984\"}]}";
        JsonObject record = (JsonObject) this.jsonParser.parse(stringRecord);
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.datesConversion.convertDatesToISO(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConvertStatus() == ConvertStatus.ERROR);
        String message = String.format(DatesConversionServiceErrorMessages.MISSING_PROPERTY, "creationDate");
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().get(0).equalsIgnoreCase(message));
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        Assert.assertEquals(record, resultRecord);
    }

    @Test
    public void shouldReturnOriginalRecordWhenPropertyValueIsNullInData() {
        String stringRecord = "{\"id\": \"unit-test-1\",\"kind\": \"unit:test:1.0.0\",\"acl\": {\"viewers\": [\"viewers@unittest.com\"],\"owners\": [\"owners@unittest.com\"]},\"legal\": {\"legaltags\": [\"unit-test-legal\"],\"otherRelevantDataCountries\": [\"US\"]},\"data\": {\"msg\": \"test record\",\"creationDate\": null},\"meta\": [{\"path\": \"\",\"kind\": \"DateTime\",\"persistableReference\": \"{\\\"type\\\": \\\"DAT\\\",\\\"format\\\": \\\"yyyy-MM-dd\\\"}\",\"propertyNames\": [\"creationDate\"],\"name\": \"GCS_WGS_1984\"}]}";
        JsonObject record = (JsonObject) this.jsonParser.parse(stringRecord);
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.datesConversion.convertDatesToISO(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConvertStatus() == ConvertStatus.ERROR);
        String message = String.format(DatesConversionServiceErrorMessages.MISSING_PROPERTY, "creationDate");
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().get(0).equalsIgnoreCase(message));
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        Assert.assertEquals(record, resultRecord);
    }

    @Test
    public void shouldReturnOriginalRecordWhenPropertyIsBadInData() {
        String stringRecord = "{\"id\": \"unit-test-1\",\"kind\": \"unit:test:1.0.0\",\"acl\": {\"viewers\": [\"viewers@unittest.com\"],\"owners\": [\"owners@unittest.com\"]},\"legal\": {\"legaltags\": [\"unit-test-legal\"],\"otherRelevantDataCountries\": [\"US\"]},\"data\": {\"msg\": \"test record\",\"creationDate\": \"Bad\"},\"meta\": [{\"path\": \"\",\"kind\": \"DateTime\",\"persistableReference\": \"{\\\"type\\\": \\\"DAT\\\",\\\"format\\\": \\\"yyyy-MM-dd\\\"}\",\"propertyNames\": [\"creationDate\"],\"name\": \"GCS_WGS_1984\"}]}";
        JsonObject record = (JsonObject) this.jsonParser.parse(stringRecord);
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.datesConversion.convertDatesToISO(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConvertStatus() == ConvertStatus.ERROR);
        String message = String.format(DatesConversionServiceErrorMessages.MISMATCH_REFERENCE, "creationDate");
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().get(0).equalsIgnoreCase(message));
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        Assert.assertEquals(record, resultRecord);
    }

    @Test
    public void shouldReturnOriginalRecordWhenReferenceDoesNotMatchData() {
        String stringRecord = "{\"id\": \"unit-test-1\",\"kind\": \"unit:test:1.0.0\",\"acl\": {\"viewers\": [\"viewers@unittest.com\"],\"owners\": [\"owners@unittest.com\"]},\"legal\": {\"legaltags\": [\"unit-test-legal\"],\"otherRelevantDataCountries\": [\"US\"]},\"data\": {\"msg\": \"test record\",\"creationDate\": \"08/03/2019 04:12:22\"},\"meta\": [{\"path\": \"\",\"kind\": \"DateTime\",\"persistableReference\": \"{\\\"type\\\": \\\"DAT\\\",\\\"format\\\": \\\"MM/dd/yyyy H.m.ss\\\"}\",\"propertyNames\": [\"creationDate\"],\"name\": \"GCS_WGS_1984\"}]}";
        JsonObject record = (JsonObject) this.jsonParser.parse(stringRecord);
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.datesConversion.convertDatesToISO(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConvertStatus() == ConvertStatus.ERROR);
        String message = String.format(DatesConversionServiceErrorMessages.MISMATCH_REFERENCE, "creationDate");
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().get(0).equalsIgnoreCase(message));
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        Assert.assertEquals(record, resultRecord);
    }

    @Test
    public void shouldReturnOriginalRecordWhenGivenInvalidDateTimeFormatter() {
        String stringRecord = "{\"id\": \"unit-test-1\",\"kind\": \"unit:test:1.0.0\",\"acl\": {\"viewers\": [\"viewers@unittest.com\"],\"owners\": [\"owners@unittest.com\"]},\"legal\": {\"legaltags\": [\"unit-test-legal\"],\"otherRelevantDataCountries\": [\"US\"]},\"data\": {\"msg\": \"test record\",\"creationDate\": \"08/033/2019 04:12:22\"},\"meta\": [{\"path\": \"\",\"kind\": \"DateTime\",\"persistableReference\": \"{\\\"type\\\": \\\"DAT\\\",\\\"format\\\": \\\"MM/ddd/yyyy H.m.ss\\\"}\",\"propertyNames\": [\"creationDate\"],\"name\": \"GCS_WGS_1984\"}]}";
        JsonObject record = (JsonObject) this.jsonParser.parse(stringRecord);
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.datesConversion.convertDatesToISO(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConvertStatus() == ConvertStatus.ERROR);
        String message = String.format(DatesConversionServiceErrorMessages.INVALID_FORMATTER, "Too many pattern letters: d");
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().get(0).equalsIgnoreCase(message));
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        Assert.assertEquals(record, resultRecord);
    }

    @Test
    public void shouldReturnOriginalRecordWhenGivenInvalidMonthValue() {
        String stringRecord = "{\"id\": \"unit-test-1\",\"kind\": \"unit:test:1.0.0\",\"acl\": {\"viewers\": [\"viewers@unittest.com\"],\"owners\": [\"owners@unittest.com\"]},\"legal\": {\"legaltags\": [\"unit-test-legal\"],\"otherRelevantDataCountries\": [\"US\"]},\"data\": {\"msg\": \"test record\",\"creationDate\": \"26/03/2019 04:12:22\"},\"meta\": [{\"path\": \"\",\"kind\": \"DateTime\",\"persistableReference\": \"{\\\"type\\\": \\\"DAT\\\",\\\"format\\\": \\\"MM/dd/yyyy HH:mm:ss\\\"}\",\"propertyNames\": [\"creationDate\"],\"name\": \"GCS_WGS_1984\"}]}";
        JsonObject record = (JsonObject) this.jsonParser.parse(stringRecord);
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.datesConversion.convertDatesToISO(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConvertStatus() == ConvertStatus.ERROR);
        String message = String.format(DatesConversionServiceErrorMessages.INVALID_DATETIME_VALUE, "26/03/2019 04:12:22", "creationDate", "Invalid value for MonthOfYear (valid values 1 - 12): 26");
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().get(0).equalsIgnoreCase(message));
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        Assert.assertEquals(record, resultRecord);
    }

    @Test
    public void shouldReturnOriginalRecordWhenGivenInvalidTimeZone() {
        String stringRecord = "{\"id\": \"unit-test-1\",\"kind\": \"unit:test:1.0.0\",\"acl\": {\"viewers\": [\"viewers@unittest.com\"],\"owners\": [\"owners@unittest.com\"]},\"legal\": {\"legaltags\": [\"unit-test-legal\"],\"otherRelevantDataCountries\": [\"US\"]},\"data\": {\"msg\": \"test record\",\"creationDate\": \"2019-08-03T13:56:22Z\"},\"meta\": [{\"path\": \"\",\"kind\": \"DateTime\",\"persistableReference\": \"{\\\"type\\\": \\\"DTM\\\",\\\"format\\\": \\\"yyyy-MM-ddTHH:mm:ssZ\\\",\\\"timeZone\\\": \\\"Invalid Timezone\\\"}\",\"propertyNames\": [\"creationDate\"],\"name\": \"GCS_WGS_1984\"}]}";
        JsonObject record = (JsonObject) this.jsonParser.parse(stringRecord);
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.datesConversion.convertDatesToISO(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConvertStatus() == ConvertStatus.ERROR);
        String message = String.format(DatesConversionServiceErrorMessages.INVALID_TIMEZONE, "Invalid ID for region-based ZoneId, invalid format: Invalid Timezone");
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().get(0).equalsIgnoreCase(message));
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        Assert.assertEquals(record, resultRecord);
    }

    @Test
    public void shouldReturnOriginalRecordWhenGivenInsufficientDateTimeInfo() {
        String stringRecord = "{\"id\": \"unit-test-1\",\"kind\": \"unit:test:1.0.0\",\"acl\": {\"viewers\": [\"viewers@unittest.com\"],\"owners\": [\"owners@unittest.com\"]},\"legal\": {\"legaltags\": [\"unit-test-legal\"],\"otherRelevantDataCountries\": [\"US\"]},\"data\": {\"msg\": \"test record\",\"creationDate\": \"13:56\"},\"meta\": [{\"path\": \"\",\"kind\": \"DateTime\",\"persistableReference\": \"{\\\"type\\\": \\\"DTM\\\",\\\"format\\\": \\\"HH:mm\\\",\\\"timeZone\\\": \\\"Antarctica/South_Pole\\\"}\",\"propertyNames\": [\"creationDate\"],\"name\": \"GCS_WGS_1984\"}]}";
        JsonObject record = (JsonObject) this.jsonParser.parse(stringRecord);
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.datesConversion.convertDatesToISO(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConvertStatus() == ConvertStatus.ERROR);
        String message = String.format(DatesConversionServiceErrorMessages.ERROR_PARSING_VALUE, "13:56");
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().get(0).equalsIgnoreCase(message));
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        Assert.assertEquals(record, resultRecord);
    }
    
    @Test
    public void shouldReturnUpdatedRecordWhenDateMetaAndDataAreValid() {
        String stringRecord = "{\"id\": \"unit-test-1\",\"kind\": \"unit:test:1.0.0\",\"acl\": {\"viewers\": [\"viewers@unittest.com\"],\"owners\": [\"owners@unittest.com\"]},\"legal\": {\"legaltags\": [\"unit-test-legal\"],\"otherRelevantDataCountries\": [\"US\"]},\"data\": {\"msg\": \"test record\",\"creationDate\": \"2019-08-03\"},\"meta\": [{\"path\": \"\",\"kind\": \"DateTime\",\"persistableReference\": \"{\\\"type\\\": \\\"DAT\\\",\\\"format\\\": \\\"yyyy-MM-dd\\\"}\",\"propertyNames\": [\"creationDate\"],\"name\": \"GCS_WGS_1984\"}]}";
        JsonObject record = (JsonObject) this.jsonParser.parse(stringRecord);
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.datesConversion.convertDatesToISO(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().size() == 0);
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        JsonElement data = resultRecord.get("data");
        String actualCreationDateValue = data.getAsJsonObject().get("creationDate").getAsString();
        Assert.assertEquals("2019-08-03", actualCreationDateValue);
        JsonArray resultMetaArray = resultRecord.getAsJsonArray("meta");
        Assert.assertEquals(1, resultMetaArray.size());
        JsonObject resultMeta = (JsonObject)resultMetaArray.get(0);
        String resultPersistableReference = resultMeta.get("persistableReference").getAsString();
        Assert.assertEquals(BASE_DATETIME_DAT, resultPersistableReference);
    }

    @Test
    public void shouldReturnUpdatedRecordWhenDateTimeMetaAndDataAreValid() {
        String stringRecord = "{\"id\": \"unit-test-1\",\"kind\": \"unit:test:1.0.0\",\"acl\": {\"viewers\": [\"viewers@unittest.com\"],\"owners\": [\"owners@unittest.com\"]},\"legal\": {\"legaltags\": [\"unit-test-legal\"],\"otherRelevantDataCountries\": [\"US\"]},\"data\": {\"msg\": \"test record\",\"creationDate\": \"August 3, 2019 13:56:22.123\"},\"meta\": [{\"path\": \"\",\"kind\": \"DateTime\",\"persistableReference\": \"{\\\"type\\\": \\\"DTM\\\",\\\"format\\\": \\\"MMMM d, yyyy HH:mm:ss.fff\\\",\\\"timeZone\\\": \\\"UTC\\\"}\",\"propertyNames\": [\"creationDate\"],\"name\": \"GCS_WGS_1984\"}]}";
        JsonObject record = (JsonObject) this.jsonParser.parse(stringRecord);
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.datesConversion.convertDatesToISO(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().size() == 0);
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        JsonElement data = resultRecord.get("data");
        String actualCreationDateValue = data.getAsJsonObject().get("creationDate").getAsString();
        Assert.assertEquals("2019-08-03T13:56:22.123Z", actualCreationDateValue);
        JsonArray resultMetaArray = resultRecord.getAsJsonArray("meta");
        Assert.assertEquals(1, resultMetaArray.size());
        JsonObject resultMeta = (JsonObject)resultMetaArray.get(0);
        String resultPersistableReference = resultMeta.get("persistableReference").getAsString();
        Assert.assertEquals(BASE_DATETIME_DTM, resultPersistableReference);
    }

    @Test
    public void shouldReturnUpdatedRecordWhenISO8601DateTimeMetaAndDataAreValid() {
        String stringRecord = "{\"id\": \"unit-test-1\",\"kind\": \"unit:test:1.0.0\",\"acl\": {\"viewers\": [\"viewers@unittest.com\"],\"owners\": [\"owners@unittest.com\"]},\"legal\": {\"legaltags\": [\"unit-test-legal\"],\"otherRelevantDataCountries\": [\"US\"]},\"data\": {\"msg\": \"test record\",\"creationDate\": \"2019-08-03T13:56:22Z\"},\"meta\": [{\"path\": \"\",\"kind\": \"DateTime\",\"persistableReference\": \"{\\\"type\\\": \\\"DTM\\\",\\\"format\\\": \\\"yyyy-MM-ddTHH:mm:ssZ\\\",\\\"timeZone\\\": \\\"UTC\\\"}\",\"propertyNames\": [\"creationDate\"],\"name\": \"GCS_WGS_1984\"}]}";
        JsonObject record = (JsonObject) this.jsonParser.parse(stringRecord);
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.datesConversion.convertDatesToISO(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().size() == 0);
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        JsonElement data = resultRecord.get("data");
        String actualCreationDateValue = data.getAsJsonObject().get("creationDate").getAsString();
        Assert.assertEquals("2019-08-03T13:56:22.000Z", actualCreationDateValue);
        JsonArray resultMetaArray = resultRecord.getAsJsonArray("meta");
        Assert.assertEquals(1, resultMetaArray.size());
        JsonObject resultMeta = (JsonObject)resultMetaArray.get(0);
        String resultPersistableReference = resultMeta.get("persistableReference").getAsString();
        Assert.assertEquals(BASE_DATETIME_DTM, resultPersistableReference);
    }
    @Test
    public void shouldReturnUpdatedRecordWithConvertedTimezone() {
        String stringRecord = "{\"id\": \"unit-test-1\",\"kind\": \"unit:test:1.0.0\",\"acl\": {\"viewers\": [\"viewers@unittest.com\"],\"owners\": [\"owners@unittest.com\"]},\"legal\": {\"legaltags\": [\"unit-test-legal\"],\"otherRelevantDataCountries\": [\"US\"]},\"data\": {\"msg\": \"test record\",\"creationDate\": \"2019-08-03T13:56:22Z\"},\"meta\": [{\"path\": \"\",\"kind\": \"DateTime\",\"persistableReference\": \"{\\\"type\\\": \\\"DTM\\\",\\\"format\\\": \\\"yyyy-MM-ddTHH:mm:ssZ\\\",\\\"timeZone\\\": \\\"Antarctica/South_Pole\\\"}\",\"propertyNames\": [\"creationDate\"],\"name\": \"GCS_WGS_1984\"}]}";
        JsonObject record = (JsonObject) this.jsonParser.parse(stringRecord);
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.datesConversion.convertDatesToISO(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().size() == 0);
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        JsonElement data = resultRecord.get("data");
        String actualCreationDateValue = data.getAsJsonObject().get("creationDate").getAsString();
        Assert.assertEquals("2019-08-03T01:56:22.000Z", actualCreationDateValue);
        JsonArray resultMetaArray = resultRecord.getAsJsonArray("meta");
        Assert.assertEquals(1, resultMetaArray.size());
        JsonObject resultMeta = (JsonObject)resultMetaArray.get(0);
        String resultPersistableReference = resultMeta.get("persistableReference").getAsString();
        Assert.assertEquals(BASE_DATETIME_DTM, resultPersistableReference);
    }

    @Test
    public void shouldReturnUpdatedRecordWithConvertedTimezone_WhenPersistableReferenceIsJsonObject() {
        String stringRecord = "{\"id\": \"unit-test-1\",\"kind\": \"unit:test:1.0.0\",\"acl\": {\"viewers\": [\"viewers@unittest.com\"],\"owners\": [\"owners@unittest.com\"]},\"legal\": {\"legaltags\": [\"unit-test-legal\"],\"otherRelevantDataCountries\": [\"US\"]},\"data\": {\"msg\": \"test record\",\"creationDate\": \"2019-08-03T13:56:22Z\"},\"meta\": [{\"path\": \"\",\"kind\": \"DateTime\", \"persistableReference\": {\"format\": \"yyyy-MM-ddTHH:mm:ssZ\", \"timeZone\": \"UTC\", \"type\": \"DTM\"} ,\"propertyNames\": [\"creationDate\"],\"name\": \"GCS_WGS_1984\"}]}";
        JsonObject record = (JsonObject) this.jsonParser.parse(stringRecord);
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.datesConversion.convertDatesToISO(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().size() == 0);
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        JsonElement data = resultRecord.get("data");
        String actualCreationDateValue = data.getAsJsonObject().get("creationDate").getAsString();
        Assert.assertEquals("2019-08-03T13:56:22.000Z", actualCreationDateValue);
        JsonArray resultMetaArray = resultRecord.getAsJsonArray("meta");
        Assert.assertEquals(1, resultMetaArray.size());
        JsonObject resultMeta = (JsonObject) resultMetaArray.get(0);
        String resultPersistableReference = resultMeta.get("persistableReference").getAsString();
        Assert.assertEquals(BASE_DATETIME_DTM, resultPersistableReference);
    }

    @Test
    public void shouldReturnUpdatedRecordWhenDatePresentedIntoNestedJsonObject() {
        String convertedDateValue = "2019-08-03T13:56:22.123Z";
        String stringRecord = "{\"id\": \"unit-test-1\",\"kind\": \"unit:test:1.0.0\",\"acl\": {\"viewers\": [\"viewers@unittest.com\"],\"owners\": [\"owners@unittest.com\"]},\"legal\": {\"legaltags\": [\"unit-test-legal\"],\"otherRelevantDataCountries\": [\"US\"]},\"data\": {\"msg\": \"test record\",\"internal\":{\"creationDate\": \"August 3, 2019 13:56:22.123\"}},\"meta\": [{\"path\": \"\",\"kind\": \"DateTime\",\"persistableReference\": \"{\\\"type\\\": \\\"DTM\\\",\\\"format\\\": \\\"MMMM d, yyyy HH:mm:ss.fff\\\",\\\"timeZone\\\": \\\"UTC\\\"}\",\"propertyNames\": [\"internal.creationDate\"],\"name\": \"GCS_WGS_1984\"}]}";
        JsonObject record = (JsonObject) this.jsonParser.parse(stringRecord);
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.datesConversion.convertDatesToISO(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().size() == 0);
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        JsonElement data = resultRecord.get("data");
        String actualCreationDateValue = data.getAsJsonObject().get("internal").getAsJsonObject().get("creationDate").getAsString();
        Assert.assertEquals(convertedDateValue, actualCreationDateValue);
        JsonArray resultMetaArray = resultRecord.getAsJsonArray("meta");
        Assert.assertEquals(1, resultMetaArray.size());
        JsonObject resultMeta = (JsonObject)resultMetaArray.get(0);
        String resultPersistableReference = resultMeta.get("persistableReference").getAsString();
        Assert.assertEquals(BASE_DATETIME_DTM, resultPersistableReference);
    }

    @Test
    public void shouldReturnUpdatedRecordWhenDatePresentedIntoNestedJsonArray() {
        String convertedDateValue = "2019-08-03T13:56:22.123Z";
        String originalDateValue = "August 3, 2019 13:56:22.123";
        String stringRecord = "{\"id\": \"unit-test-1\",\"kind\": \"unit:test:1.0.0\",\"acl\": {\"viewers\": [\"viewers@unittest.com\"],\"owners\": [\"owners@unittest.com\"]},\"legal\": {\"legaltags\": [\"unit-test-legal\"],\"otherRelevantDataCountries\": [\"US\"]},\"data\": {\"msg\": \"test record\",\"internal\":[{\"creationDate\": \"August 3, 2019 13:56:22.123\"}, {\"creationDate\": \"August 3, 2019 13:56:22.123\"}]},\"meta\": [{\"path\": \"\",\"kind\": \"DateTime\",\"persistableReference\": \"{\\\"type\\\": \\\"DTM\\\",\\\"format\\\": \\\"MMMM d, yyyy HH:mm:ss.fff\\\",\\\"timeZone\\\": \\\"UTC\\\"}\",\"propertyNames\": [\"internal[0].creationDate\"],\"name\": \"GCS_WGS_1984\"}]}";
        JsonObject record = (JsonObject) this.jsonParser.parse(stringRecord);
        List<ConversionRecord> conversionRecords = new ArrayList<>();
        ConversionRecord conversionRecord = new ConversionRecord();
        conversionRecord.setRecordJsonObject(record);
        conversionRecords.add(conversionRecord);
        this.datesConversion.convertDatesToISO(conversionRecords);
        Assert.assertEquals(1, conversionRecords.size());
        Assert.assertTrue(conversionRecords.get(0).getConversionMessages().size() == 0);
        JsonObject resultRecord = conversionRecords.get(0).getRecordJsonObject();
        JsonElement data = resultRecord.get("data");
        String convertedCreationDateValue = data.getAsJsonObject().get("internal").getAsJsonArray().get(0).getAsJsonObject().get("creationDate").getAsString();
        Assert.assertEquals(convertedDateValue, convertedCreationDateValue);
        String notConvertedCreationDateValue = data.getAsJsonObject().get("internal").getAsJsonArray().get(1).getAsJsonObject().get("creationDate").getAsString();
        Assert.assertEquals(originalDateValue, notConvertedCreationDateValue);
        JsonArray resultMetaArray = resultRecord.getAsJsonArray("meta");
        Assert.assertEquals(1, resultMetaArray.size());
        JsonObject resultMeta = (JsonObject)resultMetaArray.get(0);
        String resultPersistableReference = resultMeta.get("persistableReference").getAsString();
        Assert.assertEquals(BASE_DATETIME_DTM, resultPersistableReference);
    }

}