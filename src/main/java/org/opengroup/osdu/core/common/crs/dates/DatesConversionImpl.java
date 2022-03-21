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
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import org.opengroup.osdu.core.common.model.crs.ConversionRecord;
import org.opengroup.osdu.core.common.model.crs.ConvertStatus;
import org.opengroup.osdu.core.common.model.units.IDateTime;
import org.opengroup.osdu.core.common.util.JsonUtils;

import java.time.DateTimeException;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.opengroup.osdu.core.common.model.units.ReferenceConverter.parseDateTimeReference;
import static org.opengroup.osdu.core.common.util.JsonUtils.getJsonPropertyValueFromJsonObject;
import static org.opengroup.osdu.core.common.util.JsonUtils.overrideNestedStringPropertyOfJsonObject;

public class DatesConversionImpl {
    private static final String KIND = "kind";
    private static final String META = "meta";
    private static final String DATA = "data";
    private static final String DATETIME = "dateTime";
    private static final String PROPERTY_NAMES = "propertyNames";
    private static final String PERSISTABLE_REFERENCE = "persistableReference";
    private static final String INVALID_VALUE_STRING = "Invalid value";
    private static final String PARSE_ERROR_STRING = "could not be parsed at index";


    public void convertDatesToISO(List<ConversionRecord> conversionRecords) {
        for (int i = 0; i < conversionRecords.size(); i++) {
            this.convertDateOrDatetimeToISO(conversionRecords.get(i));
        }
    }

    private void convertDateOrDatetimeToISO(ConversionRecord conversionRecord) {
        if (conversionRecord == null) {
            return;
        }

        List<String> conversionMessages = conversionRecord.getConversionMessages();
        JsonObject record = conversionRecord.getRecordJsonObject();
        if (record == null) {
            return;
        }

        JsonObject dataBlock = record.getAsJsonObject(DATA);
        if (dataBlock == null) {
            conversionMessages.add(DatesConversionServiceErrorMessages.MISSING_DATA_BLOCK);
            conversionRecord.setConvertStatus(ConvertStatus.ERROR);
            conversionRecord.setConversionMessages(conversionMessages);
            return;
        }

        JsonArray metaArray = record.getAsJsonArray(META);
        if (metaArray == null) {
            conversionRecord.setConvertStatus(ConvertStatus.NO_FRAME_OF_REFERENCE);
            return;
        }

        boolean hasFailure = false;
        Iterator<JsonElement> metaIterator = metaArray.iterator();
        while (metaIterator.hasNext()) {
            JsonObject meta = (JsonObject) metaIterator.next();
            if (meta == null) {
                return;
            }

            JsonElement kind = meta.get(KIND);
            if (kind == null || kind.getAsString().isEmpty()) {
                hasFailure = true;
                conversionMessages.add(DatesConversionServiceErrorMessages.MISSING_META_KIND);
                continue;
            }

            String type = kind.getAsString();

            if (type.equalsIgnoreCase(DATETIME)) {
                JsonElement propertyNamesElement = meta.get(PROPERTY_NAMES);
                if (propertyNamesElement == null) {
                    hasFailure = true;
                    conversionMessages.add(DatesConversionServiceErrorMessages.MISSING_PROPERTY_NAMES);
                    continue;
                }

                JsonArray propertyNames = null;
                try {
                    propertyNames = propertyNamesElement.getAsJsonArray();
                } catch (IllegalStateException ex) {
                    propertyNames = null;
                }

                if (propertyNames == null) {
                    hasFailure = true;
                    conversionMessages.add(DatesConversionServiceErrorMessages.ILLEGAL_PROPERTY_NAMES);
                    continue;
                }

                JsonElement referenceElement = meta.get(PERSISTABLE_REFERENCE);
                if (referenceElement == null || referenceElement.toString().isEmpty()) {
                    hasFailure = true;
                    conversionMessages.add(DatesConversionServiceErrorMessages.MISSING_REFERENCE);
                    continue;
                }

                String reference = JsonUtils.jsonElementToString(referenceElement);
                IDateTime dateTime = parseDateTimeReference(reference);
                if (dateTime == null || !dateTime.isValid()) {
                    hasFailure = true;
                    conversionMessages.add(DatesConversionServiceErrorMessages.INVALID_REFERENCE);
                    continue;
                }

                boolean datesConverted = false;
                for (int i = 0; i < propertyNames.size(); i++) {
                    String name = propertyNames.get(i).getAsString();
                    String message = null;
                    try {
                        List<JsonElement> valueElements = getJsonPropertyValueFromJsonObject(name, dataBlock);
                        for (JsonElement valueElement: valueElements) {
                            if ((valueElement == null) || (valueElement instanceof JsonNull)) {
                                hasFailure = true;
                                conversionMessages.add(String.format(DatesConversionServiceErrorMessages.MISSING_PROPERTY, name));
                                continue;
                            }
                            String value = dateTime.convertToIsoDateTime(valueElement.getAsString());
                            if (value == null) {
                                hasFailure = true;
                                conversionMessages.add(String.format(DatesConversionServiceErrorMessages.INVALID_REFERENCE));
                                continue;
                            }
                            overrideNestedStringPropertyOfJsonObject(name, Arrays.asList(value), dataBlock);
                            datesConverted = true;
                        }

                    } catch(IllegalArgumentException ccEx) {
                        message = String.format(DatesConversionServiceErrorMessages.INVALID_FORMATTER, ccEx.getMessage());
                    } catch(DateTimeParseException ccEx) {
                        String exceptionMessage = ccEx.getMessage();
                        if (exceptionMessage.contains(PARSE_ERROR_STRING)) {
                            message = String.format(DatesConversionServiceErrorMessages.MISMATCH_REFERENCE, name);
                        } else if (exceptionMessage.contains(INVALID_VALUE_STRING)) {
                            String description = exceptionMessage.substring(exceptionMessage.indexOf(INVALID_VALUE_STRING));
                            message = String.format(DatesConversionServiceErrorMessages.INVALID_DATETIME_VALUE, ccEx.getParsedString(), name , description);
                        } else {
                            message = String.format(DatesConversionServiceErrorMessages.ERROR_PARSING_VALUE, ccEx.getParsedString());
                        }
                    } catch(DateTimeException ccEx) {
                        message = String.format(DatesConversionServiceErrorMessages.INVALID_TIMEZONE, ccEx.getMessage());
                    }

                    if(message != null) {
                        hasFailure = true;
                        conversionMessages.add(message);
                    }
                }

                if (datesConverted && !hasFailure) {
                    String basePersistableReference = dateTime.getBaseDateTime();
                    meta.remove(PERSISTABLE_REFERENCE);
                    meta.addProperty(PERSISTABLE_REFERENCE, basePersistableReference);
                }
            }
        }

        if (hasFailure) {
            conversionRecord.setConvertStatus(ConvertStatus.ERROR);
        }
        conversionRecord.setConversionMessages(conversionMessages);
    }
}

