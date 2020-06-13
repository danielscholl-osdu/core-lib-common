// Copyright 2017-2019, Schlumberger
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

package org.opengroup.osdu.core.common.model.storage;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.opengroup.osdu.core.common.crs.CrsConversionServiceErrorMessages;
import org.opengroup.osdu.core.common.model.crs.ConvertStatus;
import org.opengroup.osdu.core.common.search.Preconditions;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversionStatus {
    private String id;
    private String status;
    private List<String> errors;


    public static class ConversionStatusBuilder {
        private String id;
        private String status;
        private List<String> errors = new ArrayList<>();
        private List<JsonObject> validMetaItems = new ArrayList<>();
        private static final String KIND = "kind";
        private static final String PROPERTY_NAMES = "propertyNames";
        private static final String PERSISTABLE_REFERENCE = "persistableReference";

        public ConversionStatusBuilder id(String recordId) {
            this.id = recordId;
            return this;
        }

        public ConversionStatusBuilder status(String generalStatus) {
            this.status = generalStatus;
            return this;
        }

        public ConversionStatusBuilder addError(String errMsg) {
            this.errors.add(errMsg);
            this.status = ConvertStatus.ERROR.toString();
            return this;
        }

        public ConversionStatusBuilder addCRSBadRequestError(String errMsg, String propertyX, String propertyY) {
            StringBuilder properties = new StringBuilder();
            properties.append(propertyX)
                    .append(",")
                    .append(propertyY);
            this.errors.add(String.format(CrsConversionServiceErrorMessages.BAD_REQUEST_FROM_CRS, errMsg, properties.toString()));
            this.status = ConvertStatus.ERROR.toString();
            return this;
        }

        public ConversionStatusBuilder addMessage(String msg) {
            this.errors.add(msg);
            return this;
        }

        public List<JsonObject> getValidMetaItems() {
            return this.validMetaItems;
        }

        public String getId() {
            return this.id;
        }

        public String getStatus() {
            return this.status;
        }

        public List<String> getErrors() {
            return this.errors;
        }

        public ConversionStatusBuilder addErrorsFromMetaItemChecking(JsonObject metaItem) {
            boolean addToValidMetaItems = true;
            String affectedProperties = "Property information not available from metaItem";
            try {
                StringBuilder affectedPropertiesBuilder = new StringBuilder().append(" Affected properties: ");
                JsonElement propertyNamesElement = metaItem.get(PROPERTY_NAMES);
                JsonArray propertyNames = null;
                try {
                    propertyNames = propertyNamesElement.getAsJsonArray();
                    for (JsonElement property : propertyNames) {
                        affectedPropertiesBuilder.append(property.getAsString()).append(",");
                    }
                    affectedPropertiesBuilder.deleteCharAt(affectedPropertiesBuilder.length() - 1);
                    affectedProperties = affectedPropertiesBuilder.toString();
                } catch (IllegalStateException ex) {
                    this.errors.add(CrsConversionServiceErrorMessages.ILLEGAL_PROPERTY_NAMES);
                    addToValidMetaItems = false;
                }

                if (propertyNames == null || propertyNames.size() == 0) {
                    this.errors.add(CrsConversionServiceErrorMessages.MISSING_PROPERTY_NAMES);
                    addToValidMetaItems = false;
                }
                JsonElement kind = metaItem.get(KIND);
                if (kind == null || kind.getAsString().isEmpty()) {
                    this.errors.add(CrsConversionServiceErrorMessages.MISSING_META_KIND + affectedProperties);
                    addToValidMetaItems = false;
                }
                JsonElement persistableReferenceElement = metaItem.get(PERSISTABLE_REFERENCE);
                if (persistableReferenceElement == null || persistableReferenceElement.getAsString().isEmpty()) {
                    this.errors.add(CrsConversionServiceErrorMessages.MISSING_REFERENCE + affectedProperties);
                    addToValidMetaItems = false;
                }
            } catch (Exception e) {
                this.errors.add(String.format(CrsConversionServiceErrorMessages.ILLEGAL_METAITEM_ARRAY, e.getMessage()) + affectedProperties);
                addToValidMetaItems = false;
            }

            if (addToValidMetaItems) {
                validMetaItems.add(metaItem);
            } else {
                this.status = ConvertStatus.ERROR.toString();
            }

            return this;
        }

        public ConversionStatus build() {
            Preconditions.checkNotNull(this.id, "record id must be provided");
            Preconditions.checkNotNull(this.status, "record conversion status must be provided");
            Preconditions.checkNotNull(this.errors, "conversion errors must be provided");

            ConversionStatus conversionStatus = new ConversionStatus();
            conversionStatus.setId(this.id);
            conversionStatus.setStatus(this.status);
            conversionStatus.setErrors(this.errors);

            return conversionStatus;
        }
    }
}
