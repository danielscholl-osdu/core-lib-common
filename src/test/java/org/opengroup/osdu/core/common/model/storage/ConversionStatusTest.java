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

package org.opengroup.osdu.core.common.model.storage;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.opengroup.osdu.core.common.crs.CrsConversionServiceErrorMessages;

@RunWith(MockitoJUnitRunner.class)
public class ConversionStatusTest {
    private JsonParser jsonParser = new JsonParser();
    private static final String METAITEM_GOOD = "{\"path\":\"\",\"kind\":\"CRS\",\"persistableReference\": { \"scaleOffset\": {\"scale\": 0.3048, \"offset\": 0 }, \"symbol\": \"ft/s\", \"baseMeasurement\": { \"type\": \"UM\", \"ancestry\": \"Velocity\" }, \"type\": \"USO\" },\"propertyNames\":[\"X\",\"Y\",\"Z\"],\"name\":\"GCS_WGS_1984\"}";
    private static final String METAITEM_MISSING_KIND = "{\"path\":\"\",\"persistableReference\":\"reference\",\"propertyNames\":[\"X\",\"Y\",\"Z\"],\"name\":\"GCS_WGS_1984\"}";
    private static final String METAITEM_MISSING_PERSISTABLE = "{\"path\":\"\",\"kind\":\"CRS\",\"propertyNames\":[\"X\",\"Y\",\"Z\"],\"name\":\"GCS_WGS_1984\"}";
    private static final String METAITEM_MISSING_PROPERTY = "{\"path\":\"\",\"kind\":\"CRS\",\"persistableReference\":\"reference\",\"name\":\"GCS_WGS_1984\"}";

    @Test
    public void should_buildInstance_withIdAndStatusProvided() {
        ConversionStatus conversionStatus = ConversionStatus.builder().id("test-id").status("SUCCESS").build();

        Assert.assertEquals("test-id", conversionStatus.getId());
        Assert.assertEquals("SUCCESS", conversionStatus.getStatus());
        Assert.assertNotNull(conversionStatus.getErrors());
    }

    @Test
    public void should_checkingMetaItemsAndAddValidOnesToBuilder() {
        ConversionStatus.ConversionStatusBuilder statusBuilder = ConversionStatus.builder().id("test-id").status("SUCCESS");
        Assert.assertEquals(0, statusBuilder.getErrors().size());

        JsonObject metaItemGood = this.jsonParser.parse(METAITEM_GOOD).getAsJsonObject();
        statusBuilder.addErrorsFromMetaItemChecking(metaItemGood);
        Assert.assertEquals(0, statusBuilder.getErrors().size());
        Assert.assertEquals(1, statusBuilder.getValidMetaItems().size());
        Assert.assertEquals("SUCCESS", statusBuilder.getStatus());

        JsonObject metaItemKind = this.jsonParser.parse(METAITEM_MISSING_KIND).getAsJsonObject();
        statusBuilder.addErrorsFromMetaItemChecking(metaItemKind);
        Assert.assertEquals(1, statusBuilder.getErrors().size());
        Assert.assertEquals(1, statusBuilder.getValidMetaItems().size());
        Assert.assertEquals("ERROR", statusBuilder.getStatus());

        JsonObject metaItemPersistable = this.jsonParser.parse(METAITEM_MISSING_PERSISTABLE).getAsJsonObject();
        statusBuilder.addErrorsFromMetaItemChecking(metaItemPersistable);
        Assert.assertEquals(2, statusBuilder.getErrors().size());
        Assert.assertEquals(1, statusBuilder.getValidMetaItems().size());
        Assert.assertEquals("ERROR", statusBuilder.getStatus());

        JsonObject metaItemProperty = this.jsonParser.parse(METAITEM_MISSING_PROPERTY).getAsJsonObject();
        statusBuilder.addErrorsFromMetaItemChecking(metaItemProperty);
        Assert.assertEquals(3, statusBuilder.getErrors().size());
        Assert.assertEquals(1, statusBuilder.getValidMetaItems().size());
        Assert.assertEquals("ERROR", statusBuilder.getStatus());
    }

    @Test
    public void should_addErrorsAndPropertyNamesToBuilder() {
        ConversionStatus.ConversionStatusBuilder statusBuilder = ConversionStatus.builder().id("test-id").status("SUCCESS");
        Assert.assertEquals(0, statusBuilder.getErrors().size());

        statusBuilder.addCRSBadRequestError("Bad Request From Crs", "x", "y");
        Assert.assertEquals(1, statusBuilder.getErrors().size());
        Assert.assertEquals(String.format(CrsConversionServiceErrorMessages.BAD_REQUEST_FROM_CRS, "Bad Request From Crs", "x,y"), statusBuilder.getErrors().get(0));
    }

    @Test
    public void should_setStatusAsError_whenAddErrors() {
        ConversionStatus.ConversionStatusBuilder statusBuilder = ConversionStatus.builder().id("test-id").status("SUCCESS");
        Assert.assertEquals(0, statusBuilder.getErrors().size());
        Assert.assertEquals("SUCCESS", statusBuilder.getStatus());

        statusBuilder.addError("error");
        Assert.assertEquals(1, statusBuilder.getErrors().size());
        Assert.assertEquals("error", statusBuilder.getErrors().get(0));
        Assert.assertEquals("ERROR", statusBuilder.getStatus());
    }

    @Test
    public void should_notSetStatusAsError_whenAddMsgs() {
        ConversionStatus.ConversionStatusBuilder statusBuilder = ConversionStatus.builder().id("test-id").status("SUCCESS");
        Assert.assertEquals(0, statusBuilder.getErrors().size());
        Assert.assertEquals("SUCCESS", statusBuilder.getStatus());

        statusBuilder.addMessage("message");
        Assert.assertEquals(1, statusBuilder.getErrors().size());
        Assert.assertEquals("message", statusBuilder.getErrors().get(0));
        Assert.assertEquals("SUCCESS", statusBuilder.getStatus());
    }
}
