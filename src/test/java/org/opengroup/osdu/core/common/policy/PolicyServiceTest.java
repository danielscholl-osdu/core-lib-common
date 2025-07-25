// Copyright © Schlumberger
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

package org.opengroup.osdu.core.common.policy;

import static org.mockito.ArgumentMatchers.any;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.opengroup.osdu.core.common.http.HttpClient;
import org.opengroup.osdu.core.common.http.HttpRequest;
import org.opengroup.osdu.core.common.http.HttpResponse;
import org.opengroup.osdu.core.common.http.json.HttpResponseBodyMapper;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.model.policy.BatchPolicyResponse;
import org.opengroup.osdu.core.common.model.policy.PolicyRequest;
import org.opengroup.osdu.core.common.model.policy.PolicyResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class PolicyServiceTest {

    private static final String ROOT_URL = "http://example.com";
    @Spy
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Spy
    private final HttpResponseBodyMapper responseBodyMapper = new HttpResponseBodyMapper(objectMapper);
    @Mock
    private PolicyAPIConfig policyAPIConfig;
    @Mock
    private HttpClient httpClient;
    @Mock
    private DpsHeaders headers;
    @InjectMocks
    private PolicyService policyService;

    @Test
    public void should_allow_when_validPolicyProvided() throws PolicyException {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setResponseCode(200);
        httpResponse.setBody("{\n" +
                "    \"result\": {\n" +
                "        \"allow\": true\n" +
                "    }\n" +
                "}");
        Mockito.when(httpClient.send(any())).thenReturn(httpResponse);

        PolicyRequest policyRequest = new PolicyRequest();
        policyRequest.setPolicyId("storage");
        policyRequest.setInput(getValidPolicy());

        PolicyResponse policyResponse = policyService.evaluatePolicy(policyRequest);
        Assert.assertNotNull(policyResponse);
        ArgumentCaptor<HttpRequest> httpRequestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        Mockito.verify(httpClient, Mockito.times(1)).send(httpRequestCaptor.capture());
    }

    @Test
    public void getCompiledPolicy_allow_translateESEndpoint() throws PolicyException {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setResponseCode(200);
        httpResponse.setBody("{\n" +
                "        \"query\": {\n" +
                "            \"bool\": {\n" +
                "                \"should\": [\n" +
                "                    {\"bool\": {\"filter\": [{\"terms\": {\"acl.owners\": [\"AAA\", \"BBB\"]}}]}},\n" +
                "                    {\"bool\": {\"filter\": [{\"terms\": {\"acl.viewers\": [\"AAA\", \"BBB\"]}}]}}\n" +
                "                ]\n" +
                "            }\n" +
                "        }\n" +
                "    }");
        Mockito.when(httpClient.send(any())).thenReturn(httpResponse);


        Map<String, Object> userData = new HashMap<>();
        userData.put("groups", new String[]{"AAA", "BBB"});
        Map<String, Object> input = new HashMap<>();
        input.put("user", userData);
        input.put("operation", "view");
        ArrayList <String> unknownsList = new ArrayList<>();
        unknownsList.add("input.record");

        String policyResponse = policyService.getCompiledPolicy(
                "data.search.allow == true",
                unknownsList,
                input
        );
        Assert.assertNotNull(policyResponse);
        ArgumentCaptor<HttpRequest> httpRequestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        Mockito.verify(httpClient, Mockito.times(1)).send(httpRequestCaptor.capture());
    }

    @Test
    public void should_allow_when_validBatchPolicyProvided() throws PolicyException {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setResponseCode(200);
        httpResponse.setBody("{\n" +
                "    \"result\": {\n" +
                "        \"allowed_records\": [\"opendes:doc:e06d768d3e1c496da3274014fdc70ec9\"]\n" +
                "    }\n" +
                "}");
        Mockito.when(httpClient.send(any())).thenReturn(httpResponse);

        PolicyRequest policyRequest = new PolicyRequest();
        policyRequest.setPolicyId("search");
        policyRequest.setInput(getValidBatchPolicy());

        BatchPolicyResponse batchPolicyResponse = policyService.evaluateBatchPolicy(policyRequest);
        Assert.assertNotNull(batchPolicyResponse);
        ArgumentCaptor<HttpRequest> httpRequestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        Mockito.verify(httpClient, Mockito.times(1)).send(httpRequestCaptor.capture());
    }

    private JsonObject getValidPolicy() {
        String storagePolicy = "{\n" +
                "   \"policyId\": \"storage\",\n" +
                "   \"input\": {\n" +
                "      \"operation\": \"view\",\n" +
                "      \"record\": {\n" +
                "         \"kind\": \"opendes:osdu:well-master:0.2.0\",\n" +
                "         \"legal\": {\n" +
                "            \"status\": \"compliant\",\n" +
                "            \"legaltags\": [\n" +
                "               \"opendes-public-usa-dataset-1\"\n" +
                "            ],\n" +
                "            \"otherRelevantDataCountries\": [\n" +
                "               \"US\"\n" +
                "            ]\n" +
                "         },\n" +
                "         \"acl\": {\n" +
                "            \"owners\": [\n" +
                "               \"data.default.owner@opendes.osdu.com\"\n" +
                "            ],\n" +
                "            \"viewers\": [\n" +
                "               \"data.default.viewer@opendes.osdu.com\"\n" +
                "            ]\n" +
                "         },\n" +
                "         \"version\": 1587049754313659,\n" +
                "         \"meta\": \"None\",\n" +
                "         \"createUser\": \"daniel@cloudcodeit.com\",\n" +
                "         \"id\": \"opendes:doc:e06d768d3e1c496da3274014fdc70ec9\",\n" +
                "         \"createTime\": \"2020-04-16T15:09:56.798Z\",\n" +
                "         \"DataSourceOrganisationID\": [\n" +
                "            \"Houston\",\n" +
                "            \"Qatar Business Unit\"\n" +
                "        ]\n" +
                "      },\n" +
                "      \"groups\": [\n" +
                "         \"service.entitlements.user@opendes.osdu.com\",\n" +
                "         \"service.storage.admin@opendes.osdu.com\",\n" +
                "         \"service.legal.admin@opendes.osdu.com\",\n" +
                "         \"service.search.user@opendes.osdu.com\",\n" +
                "         \"data.datalake.viewer@opendes.osdu.com\",\n" +
                "         \"data.default.viewer@opendes.osdu.com\",\n" +
                "         \"data.default.owner@opendes.osdu.com\"\n" +
                "      ]\n" +
                "\t}\n" +
                "}\n";
        return new JsonParser().parse(storagePolicy).getAsJsonObject();
    }

    private JsonObject getValidBatchPolicy() {
        String storagePolicy = "{\n" +
                "   \"policyId\": \"storage\",\n" +
                "   \"input\": {\n" +
                "      \"operation\": \"view\",\n" +
                "      \"records\": [{\n" +
                "         \"kind\": \"opendes:osdu:well-master:0.2.0\",\n" +
                "         \"legal\": {\n" +
                "            \"status\": \"compliant\",\n" +
                "            \"legaltags\": [\n" +
                "               \"opendes-public-usa-dataset-1\"\n" +
                "            ],\n" +
                "            \"otherRelevantDataCountries\": [\n" +
                "               \"US\"\n" +
                "            ]\n" +
                "         },\n" +
                "         \"acl\": {\n" +
                "            \"owners\": [\n" +
                "               \"data.default.owner@opendes.osdu.com\"\n" +
                "            ],\n" +
                "            \"viewers\": [\n" +
                "               \"data.default.viewer@opendes.osdu.com\"\n" +
                "            ]\n" +
                "         },\n" +
                "         \"version\": 1587049754313659,\n" +
                "         \"meta\": \"None\",\n" +
                "         \"createUser\": \"daniel@cloudcodeit.com\",\n" +
                "         \"id\": \"opendes:doc:e06d768d3e1c496da3274014fdc70ec9\",\n" +
                "         \"createTime\": \"2020-04-16T15:09:56.798Z\",\n" +
                "         \"DataSourceOrganisationID\": [\n" +
                "            \"Houston\",\n" +
                "            \"Qatar Business Unit\"\n" +
                "        ]\n" +
                "      }],\n" +
                "      \"groups\": [\n" +
                "         \"service.entitlements.user@opendes.osdu.com\",\n" +
                "         \"service.storage.admin@opendes.osdu.com\",\n" +
                "         \"service.legal.admin@opendes.osdu.com\",\n" +
                "         \"service.search.user@opendes.osdu.com\",\n" +
                "         \"data.datalake.viewer@opendes.osdu.com\",\n" +
                "         \"data.default.viewer@opendes.osdu.com\",\n" +
                "         \"data.default.owner@opendes.osdu.com\"\n" +
                "      ]\n" +
                "\t}\n" +
                "}\n";
        return new JsonParser().parse(storagePolicy).getAsJsonObject();
    }
}
