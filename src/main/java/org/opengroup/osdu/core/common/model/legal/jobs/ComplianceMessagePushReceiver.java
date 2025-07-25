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

package org.opengroup.osdu.core.common.model.legal.jobs;

import com.google.gson.Gson;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.http.RequestBodyExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class ComplianceMessagePushReceiver {

	@Autowired
	private DpsHeaders dpsHeaders;

	@Autowired
	private RequestBodyExtractor requestBodyExtractor;

	@Autowired
	private LegalTagConsistencyValidator legalTagConsistencyValidator;

	@Autowired
	private ILegalComplianceChangeService legalComplianceChangeService;

	public void receiveMessageFromHttpRequest() throws ComplianceUpdateStoppedException {
		LegalTagChangedCollection dto = new Gson().fromJson(this.requestBodyExtractor.extractDataFromRequestBody(),
				LegalTagChangedCollection.class);
		LegalTagChangedCollection validDto = this.legalTagConsistencyValidator.checkLegalTagStatusWithLegalService(dto);
		this.legalComplianceChangeService.updateComplianceOnRecords(validDto, this.dpsHeaders);
	}
}