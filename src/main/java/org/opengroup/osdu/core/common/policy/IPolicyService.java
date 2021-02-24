package org.opengroup.osdu.core.common.policy;

import org.opengroup.osdu.core.common.model.policy.PolicyRequest;
import org.opengroup.osdu.storage.model.policy.PolicyResponse;

public interface IPolicyProvider {

   PolicyResponse evaluatePolicy(PolicyRequest policy);
}
