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

package org.opengroup.osdu.core.common.logging;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.opengroup.osdu.core.common.model.AppEngineHeaders;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.model.http.HeadersToLog;
import org.opengroup.osdu.core.common.model.http.Request;
import org.opengroup.osdu.core.common.logging.audit.AuditPayload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@RequestScope
public class JaxRsDpsLog implements AutoCloseable {

	@Value("${LOG_PREFIX}")
    private String LOG_PREFIX;

	private ILogger log;
	private DpsHeaders headers;

	@Inject
	public JaxRsDpsLog(ILogger log, DpsHeaders headers){
		this.log = log;
		this.headers = headers;
	}

	public void audit(AuditPayload auditPayload) {
		log.audit(LOG_PREFIX + ".audit", auditPayload, this.getLabels());
	}

	public void audit(final String loggerName, final AuditPayload auditPayload) {
		log.audit(loggerName, LOG_PREFIX + ".audit", auditPayload, this.getLabels());
	}

	public void request(Request httpRequest) {
		log.request(LOG_PREFIX + ".request", httpRequest, this.getLabels());
	}

	public void request(final String loggerName, final Request httpRequest) {
		log.request(loggerName, LOG_PREFIX + ".request", httpRequest, this.getLabels());
	}

	public void info(String message) {
		log.info(LOG_PREFIX + ".app", message, this.getLabels());
	}

	public void info(final String loggerName, final String message) {
		log.info(loggerName, LOG_PREFIX + ".app", message, this.getLabels());
	}

	public void warning(String message) {
		log.warning(LOG_PREFIX + ".app", message, this.getLabels());
	}

	public void warning(final String loggerName, final String message) {
		log.warning(loggerName, LOG_PREFIX + ".app", message, this.getLabels());
	}

	private String prepareWarningMessage(List<String> messages) {
		int sn = 0;
		StringBuilder sb = new StringBuilder();
		for (String s : messages) {
			sb.append(String.format("%d: %s", sn++, s)).append(System.lineSeparator());
		}

		return sb.toString();
	}

	public void warning(List<String> messages) {
		if (messages == null || messages.isEmpty()) {
			return;
		}

		log.warning(LOG_PREFIX + ".app", prepareWarningMessage(messages), this.getLabels());
	}

	public void warning(final String loggerName, final List<String> messages) {
		if (messages == null || messages.isEmpty()) {
			return;
		}

		log.warning(loggerName, LOG_PREFIX + ".app", prepareWarningMessage(messages), this.getLabels());
	}

	public void warning(String message, Exception e) {
		log.warning(LOG_PREFIX + ".app", message, e, this.getLabels());
	}

	public void warning(final String loggerName, final String message, final Exception e) {
		log.warning(loggerName, LOG_PREFIX + ".app", message, e, this.getLabels());
	}

	public void error(String message) {
		log.error(LOG_PREFIX + ".app", message, this.getLabels());
	}

	public void error(final String loggerName, final String message) {
		log.error(loggerName, LOG_PREFIX + ".app", message, this.getLabels());
	}

	public void error(String message, Exception e) {
		log.error(LOG_PREFIX + ".app", message, e, this.getLabels());
	}

	public void error(final String loggerName, final String message, final Exception e) {
		log.error(loggerName, LOG_PREFIX + ".app", message, e, this.getLabels());
	}

	@Override
	public void close() throws Exception {
	}

	private Map<String, String> getLabels() {
		Map<String, String> out;
		if (headers != null) {
			out = LogUtils.createStandardLabelsFromMap(headers.getHeaders());
			if (out.containsKey(AppEngineHeaders.TASK_RETRY_COUNT)) {
				out.put(AppEngineHeaders.TASK_RETRY_COUNT, StringUtils.join(out.get(AppEngineHeaders.TASK_RETRY_COUNT), ','));
			}
			return out;
	}
		return Collections.emptyMap();
}
}
