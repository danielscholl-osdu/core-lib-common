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

import org.apache.commons.lang3.StringUtils;
import org.opengroup.osdu.core.common.http.HttpRequest;
import org.opengroup.osdu.core.common.http.HttpResponse;
import org.opengroup.osdu.core.common.http.IHttpClient;
import org.opengroup.osdu.core.common.http.json.HttpResponseBodyMapper;
import org.opengroup.osdu.core.common.http.json.HttpResponseBodyParsingException;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.model.policy.BatchPolicyResponse;
import org.opengroup.osdu.core.common.model.policy.PolicyRequest;
import org.opengroup.osdu.core.common.util.UrlNormalizationUtil;
import org.opengroup.osdu.core.common.model.policy.PolicyResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PolicyService implements IPolicyProvider {

    public PolicyService(PolicyAPIConfig config,
                  IHttpClient httpClient,
                  DpsHeaders headers,
                  HttpResponseBodyMapper bodyMapper) {
        this.rootUrl = config.getRootUrl();
        this.httpClient = httpClient;
        this.headers = headers;
        this.bodyMapper = bodyMapper;
    }

    private final String rootUrl;
    private final IHttpClient httpClient;
    private final DpsHeaders headers;
    private final HttpResponseBodyMapper bodyMapper;

    @Override
    public PolicyResponse evaluatePolicy(PolicyRequest policyRequest) throws PolicyException {
        String url = this.createUrl("/evaluations/query");
        HttpResponse result = this.httpClient.send(
                HttpRequest.post(policyRequest).url(url).headers(this.headers.getHeaders()).build());
        return this.getResult(result, PolicyResponse.class);
    }

    @Override
    public String getCompiledPolicy(String ruleToCheck, List<String> unknownsList, Map<String, Object> input) throws PolicyException {
        String url = this.createUrl("/translate");
        Map<String, Object> compileQueryBody = new HashMap<>();
        compileQueryBody.put("query", ruleToCheck);
        compileQueryBody.put("unknowns", unknownsList);
        compileQueryBody.put("input", input);
        HttpResponse result = this.httpClient.send(
                HttpRequest.post(compileQueryBody).url(url).headers(this.headers.getHeaders()).build());
        return this.getResult(result);
    }

    @Override
    public BatchPolicyResponse evaluateBatchPolicy(PolicyRequest policyRequest) throws PolicyException {
        String url = this.createUrl("/evaluations/query");
        HttpResponse result = this.httpClient.send(
                HttpRequest.post(policyRequest).url(url).headers(this.headers.getHeaders()).build());
        return this.getResult(result, BatchPolicyResponse.class);
    }

    private String createUrl(String pathAndQuery) {
        return UrlNormalizationUtil.normalizeStringUrl(this.rootUrl, pathAndQuery);
    }

    private <T> T getResult(HttpResponse result, Class<T> type) throws PolicyException {
        if (!result.isSuccessCode()) {
            throw this.generatePolicyException(result, null);
        }

        try {
            if (StringUtils.isBlank(result.getBody())) {
                return null;
            }
            return bodyMapper.parseBody(result, type);
        } catch (HttpResponseBodyParsingException e) {
            throw new PolicyException("Error parsing response. Check the inner HttpResponse for more info.", result);
        }
    }

    private String getResult(HttpResponse result) throws PolicyException {
        if (!result.isSuccessCode()) {
            String errorReason = null;
            Exception exception = result.getException();
            if (exception != null){
                errorReason = exception.getMessage();
            }
            throw this.generatePolicyException(result, errorReason);
        }
        return result.getBody();
    }

    private PolicyException generatePolicyException(HttpResponse result, String errorReason) {
        String errorMessage = "Error making request to Policy service. Check the inner HttpResponse for more info.";
        if (StringUtils.isNotBlank(errorReason)) {
            errorMessage = errorMessage + " Reason: " + errorReason;
        }
        return new PolicyException(errorMessage, result);
    }
}
