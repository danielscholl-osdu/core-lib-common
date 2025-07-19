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

package org.opengroup.osdu.core.common.legal;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.opengroup.osdu.core.common.http.HttpClient;
import org.opengroup.osdu.core.common.http.HttpRequest;
import org.opengroup.osdu.core.common.http.HttpResponse;
import org.opengroup.osdu.core.common.http.json.HttpResponseBodyMapper;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.model.legal.LegalException;
import org.opengroup.osdu.core.common.model.legal.LegalTag;
import org.opengroup.osdu.core.common.model.legal.Properties;
import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class LegalServiceTest {

    private static final String ROOT_URL = "http://example.com";
    @Spy
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Spy
    private final HttpResponseBodyMapper responseBodyMapper = new HttpResponseBodyMapper(objectMapper);
    @Mock
    private LegalAPIConfig legalAPIConfig;
    @Mock
    private HttpClient httpClient;
    @Mock
    private DpsHeaders headers;
    @InjectMocks
    private LegalService legalService;

    @Test
    public void should_returnLegalTag_when_legalTagIsCreated() throws LegalException {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setResponseCode(200);
        httpResponse.setBody("{\n" +
                "    \"name\": \"legaltagName\",\n" +
                "    \"description\": \"\",\n" +
                "    \"properties\": {\n" +
                "        \"countryOfOrigin\": [\n" +
                "            \"US\"\n" +
                "        ],\n" +
                "        \"contractId\": \"A1234\",\n" +
                "        \"expirationDate\": \"2099-12-31\",\n" +
                "        \"originator\": \"OSDU\",\n" +
                "        \"dataType\": \"Third Party Data\",\n" +
                "        \"securityClassification\": \"Public\",\n" +
                "        \"personalData\": \"No Personal Data\",\n" +
                "        \"exportClassification\": \"EAR99\"\n" +
                "    }\n" +
                "}");
        Mockito.when(httpClient.send(any())).thenReturn(httpResponse);

        LegalTag legalTag = new LegalTag();
        legalTag.setName("legaltagName");
        legalTag.setProperties(new Properties());
        legalTag.getProperties().setCountryOfOrigin(Collections.singletonList("US"));
        legalTag.getProperties().setContractId("A1234");
        legalTag.getProperties().setExpirationDate(Date.valueOf("2099-12-31"));
        legalTag.getProperties().setOriginator("OSDU");
        legalTag.getProperties().setDataType("Third Party Data");
        legalTag.getProperties().setSecurityClassification("Public");
        legalTag.getProperties().setPersonalData("No Personal Data");
        legalTag.getProperties().setExportClassification("EAR99");

        LegalTag returnedLegalTag = legalService.create(legalTag);
        Assert.assertNotNull(returnedLegalTag);
        ArgumentCaptor<HttpRequest> httpRequestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        Mockito.verify(httpClient, Mockito.times(1)).send(httpRequestCaptor.capture());
        JsonParser parser = new JsonParser();
        JsonElement expectedJson = parser.parse("{\n" +
                "    \"id\": -1,\n" +
                "    \"name\": \"legaltagName\",\n" +
                "    \"description\": \"\",\n" +
                "    \"properties\": {\n" +
                "        \"countryOfOrigin\": [\"US\"],\n" +
                "        \"contractId\": \"A1234\",\n" +
                "        \"expirationDate\": \"2099-12-31\",\n" +
                "        \"originator\": \"OSDU\",\n" +
                "        \"dataType\": \"Third Party Data\",\n" +
                "        \"securityClassification\": \"Public\",\n" +
                "        \"personalData\": \"No Personal Data\",\n" +
                "        \"exportClassification\": \"EAR99\"\n" +
                "    },\n" +
                "    \"isValid\": false\n" +
                "}");
        JsonElement actualJson = parser.parse(httpRequestCaptor.getValue().getBody());
        Assert.assertEquals(expectedJson, actualJson);
    }

    @Test
    public void testUrlNormalization () throws LegalException {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setResponseCode(200);
        Mockito.when(httpClient.send(any())).thenReturn(httpResponse);
        String malformedUrl = " \n  " + ROOT_URL + "\n // \t \f \r";
        Mockito.when(legalAPIConfig.getRootUrl()).thenReturn(malformedUrl);
        legalService = new LegalService(legalAPIConfig,httpClient,headers,responseBodyMapper);

        legalService.get("any");
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
        Mockito.verify(httpClient).send(captor.capture());
        assertEquals(ROOT_URL + "/legaltags/any",captor.getValue().getUrl());
    }
    
    @Test
    public void should_returnLegalTag_when_legalTagIsCreatedWithExtensionProperties() throws LegalException {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setResponseCode(200);
        httpResponse.setBody("{\n" +
                "    \"name\": \"legaltagName\",\n" +
                "    \"description\": \"\",\n" +
                "    \"properties\": {\n" +
                "        \"countryOfOrigin\": [\n" +
                "            \"US\"\n" +
                "        ],\n" +
                "        \"contractId\": \"A1234\",\n" +
                "        \"expirationDate\": \"2099-12-31\",\n" +
                "        \"originator\": \"OSDU\",\n" +
                "        \"dataType\": \"Third Party Data\",\n" +
                "        \"securityClassification\": \"Public\",\n" +
                "        \"personalData\": \"No Personal Data\",\n" +
                "        \"exportClassification\": \"EAR99\",\n" +
                "        \"extensionProperties\":{\n" +
                "            \"EffectiveDate\":\"2022-06-01T00:00:00\",\n" +
                "            \"AffiliateEnablementIndicator\":true,\n" +
                "            \"AgreementParties\":[\n" +
                "                {\n" +
                "                    \"AgreementPartyType\":\"EnabledAffiliate\",\n" +
                "                    \"AgreementParty\":\"osdu:master-data--Organisation:TestCompany\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    }\n" +
                "}");
        Mockito.when(httpClient.send(any())).thenReturn(httpResponse);

        LegalTag legalTag = new LegalTag();
        legalTag.setName("legaltagName");
        legalTag.setProperties(new Properties());
        legalTag.getProperties().setCountryOfOrigin(Collections.singletonList("US"));
        legalTag.getProperties().setContractId("A1234");
        legalTag.getProperties().setExpirationDate(Date.valueOf("2099-12-31"));
        legalTag.getProperties().setOriginator("OSDU");
        legalTag.getProperties().setDataType("Third Party Data");
        legalTag.getProperties().setSecurityClassification("Public");
        legalTag.getProperties().setPersonalData("No Personal Data");
        legalTag.getProperties().setExportClassification("EAR99");
        
        Map<String, Object> extensionProperties = new LinkedHashMap <String, Object>();
        extensionProperties.put("EffectiveDate", "2022-06-01T00:00:00");
        extensionProperties.put("AffiliateEnablementIndicator", true);
        Map<String, Object> agreementParty = new LinkedHashMap <String, Object>();
        agreementParty.put("AgreementPartyType", "EnabledAffiliate");
        agreementParty.put("AgreementParty", "osdu:master-data--Organisation:TestCompany");
        extensionProperties.put("AgreementParties", Arrays.asList(agreementParty));
        legalTag.getProperties().setExtensionProperties(extensionProperties);

        LegalTag returnedLegalTag = legalService.create(legalTag);
        Assert.assertNotNull(returnedLegalTag);
        ArgumentCaptor<HttpRequest> httpRequestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        Mockito.verify(httpClient, Mockito.times(1)).send(httpRequestCaptor.capture());
        JsonParser parser = new JsonParser();
        JsonElement expectedJson = parser.parse("{\n" +
                "    \"id\": -1,\n" +
                "    \"name\": \"legaltagName\",\n" +
                "    \"description\": \"\",\n" +
                "    \"properties\": {\n" +
                "        \"countryOfOrigin\": [\"US\"],\n" +
                "        \"contractId\": \"A1234\",\n" +
                "        \"expirationDate\": \"2099-12-31\",\n" +
                "        \"originator\": \"OSDU\",\n" +
                "        \"dataType\": \"Third Party Data\",\n" +
                "        \"securityClassification\": \"Public\",\n" +
                "        \"personalData\": \"No Personal Data\",\n" +
                "        \"exportClassification\": \"EAR99\",\n" +
                "        \"extensionProperties\":{\n" +
                "            \"EffectiveDate\":\"2022-06-01T00:00:00\",\n" +
                "            \"AffiliateEnablementIndicator\":true,\n" +
                "            \"AgreementParties\":[\n" +
                "                {\n" +
                "                    \"AgreementPartyType\":\"EnabledAffiliate\",\n" +
                "                    \"AgreementParty\":\"osdu:master-data--Organisation:TestCompany\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    },\n" +
                "    \"isValid\": false\n" +
                "}");
        JsonElement actualJson = parser.parse(httpRequestCaptor.getValue().getBody());
        Assert.assertEquals(expectedJson, actualJson);
    }
}
