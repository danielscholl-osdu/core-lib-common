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

package org.opengroup.osdu.core.common.model.legal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.sql.Date;
import java.util.Arrays;

public class LegalTagSerializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void shouldSuccessfullySerializeLegalTag() throws JsonProcessingException {
        final LegalTag legalTag = new LegalTag();
        legalTag.setId(123L);
        legalTag.setName("name");
        legalTag.setDescription("desc");
        final Properties properties = new Properties();
        properties.setContractId("contrId");
        properties.setDataType("dataType");
        properties.setCountryOfOrigin(Arrays.asList("US", "BY"));
        properties.setExpirationDate(Date.valueOf("2020-12-20"));
        properties.setOriginator("company");
        properties.setSecurityClassification("securityClassification");
        properties.setPersonalData("data");
        properties.setExportClassification("exportClassification");
        legalTag.setProperties(properties);

        objectMapper.setTimeZone(TimeZone.getDefault());
        String result = objectMapper.writeValueAsString(legalTag);

        Assert.assertEquals("{\"id\":123,\"name\":\"name\",\"description\":\"desc\"," +
                "\"properties\":{\"countryOfOrigin\":[\"US\",\"BY\"],\"contractId\":\"contrId\"," +
                "\"expirationDate\":\"2020-12-20\",\"originator\":\"company\"," +
                "\"dataType\":\"dataType\",\"securityClassification\":\"securityClassification\"," +
                "\"personalData\":\"data\",\"exportClassification\":\"exportClassification\"}," +
                "\"isValid\":false}", result);
    }

    @Test
    public void shouldSuccessfullyDeserializeLegalTag() throws IOException {
        final String input = "{\"id\":123,\"name\":\"name\",\"description\":\"desc\"," +
                "\"properties\":{\"countryOfOrigin\":[\"US\",\"BY\"],\"contractId\":\"contrId\"," +
                "\"expirationDate\":\"2020-12-20\",\"originator\":\"company\"," +
                "\"dataType\":\"dataType\",\"securityClassification\":\"securityClassification\"," +
                "\"personalData\":\"data\",\"exportClassification\":\"exportClassification\"}," +
                "\"isValid\":false}";

        LegalTag legalTag = objectMapper.readValue(input, LegalTag.class);

        Assert.assertEquals(new Long(123L), legalTag.getId());
        Assert.assertEquals("name", legalTag.getName());
        Assert.assertEquals("desc", legalTag.getDescription());
        Assert.assertEquals("contrId", legalTag.getProperties().getContractId());
        Assert.assertEquals(Arrays.asList("US","BY"), legalTag.getProperties().getCountryOfOrigin());
        Assert.assertEquals(Date.valueOf("2020-12-20"), legalTag.getProperties().getExpirationDate());
        Assert.assertEquals("company", legalTag.getProperties().getOriginator());
        Assert.assertEquals("dataType", legalTag.getProperties().getDataType());
        Assert.assertEquals("securityClassification", legalTag.getProperties().getSecurityClassification());
        Assert.assertEquals("data", legalTag.getProperties().getPersonalData());
        Assert.assertEquals("exportClassification", legalTag.getProperties().getExportClassification());
    }

    @Test
    public void shouldSetDefaultExpirationDateIfNotProvided() throws IOException {
        final String input = "{\"id\":123,\"name\":\"name\",\"description\":\"desc\"," +
                "\"properties\":{\"countryOfOrigin\":[\"US\",\"BY\"],\"contractId\":\"contrId\"," +
                "\"originator\":\"company\"," +
                "\"dataType\":\"dataType\",\"securityClassification\":\"securityClassification\"," +
                "\"personalData\":\"data\",\"exportClassification\":\"exportClassification\"}," +
                "\"isValid\":false}";

        LegalTag legalTag = objectMapper.readValue(input, LegalTag.class);

        Assert.assertEquals(Date.valueOf("9999-12-31"), legalTag.getProperties().getExpirationDate());
    }

    @Test
    public void shouldSetDefaultExpirationDateIfEmptyProvided() throws IOException {
        final String input = "{\"id\":123,\"name\":\"name\",\"description\":\"desc\"," +
                "\"properties\":{\"countryOfOrigin\":[\"US\",\"BY\"],\"contractId\":\"contrId\"," +
                "\"expirationDate\":\"\",\"originator\":\"company\"," +
                "\"dataType\":\"dataType\",\"securityClassification\":\"securityClassification\"," +
                "\"personalData\":\"data\",\"exportClassification\":\"exportClassification\"}," +
                "\"isValid\":false}";

        LegalTag legalTag = objectMapper.readValue(input, LegalTag.class);

        Assert.assertEquals(Date.valueOf("9999-12-31"), legalTag.getProperties().getExpirationDate());
    }

    @Test
    public void shouldParseDateInOldFormat() throws IOException {
        final String input = "{\"id\":123,\"name\":\"name\",\"description\":\"desc\"," +
                "\"properties\":{\"countryOfOrigin\":[\"US\",\"BY\"],\"contractId\":\"contrId\"," +
                "\"expirationDate\": 1570863500000,\"originator\":\"company\"," +
                "\"dataType\":\"dataType\",\"securityClassification\":\"securityClassification\"," +
                "\"personalData\":\"data\",\"exportClassification\":\"exportClassification\"}," +
                "\"isValid\":false}";

        LegalTag legalTag = objectMapper.readValue(input, LegalTag.class);

        Assert.assertEquals(Date.valueOf("2019-10-12").toLocalDate(), legalTag.getProperties().getExpirationDate().toLocalDate());
    }
}
