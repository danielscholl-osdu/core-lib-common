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

import org.opengroup.osdu.core.common.model.policy.BatchPolicyResponse;
import org.opengroup.osdu.core.common.model.policy.PolicyRequest;
import org.opengroup.osdu.core.common.model.policy.PolicyResponse;

import java.util.List;
import java.util.Map;

public interface IPolicyProvider {

    PolicyResponse evaluatePolicy(PolicyRequest policy) throws PolicyException;

    String getCompiledPolicy(String ruleToBeChecked, List<String> unknownsList, Map<String, Object> input) throws PolicyException;

    BatchPolicyResponse evaluateBatchPolicy(PolicyRequest policy) throws PolicyException;
}
