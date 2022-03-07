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

package org.opengroup.osdu.core.common.logging;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.verification.VerificationMode;
import org.opengroup.osdu.core.common.logging.audit.AuditPayload;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.model.http.Request;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Contains Tests for {@link JaxRsDpsLog}
 */
@RunWith(MockitoJUnitRunner.class)
public class JaxRsDpsLogTest {
    private static final String LOG_PREFIX = "service";
    private static final String AUDIT_LOG_PREFIX = LOG_PREFIX + ".audit";
    private static final String REQUEST_LOG_PREFIX = LOG_PREFIX + ".request";
    private static final String APP_LOG_PREFIX = LOG_PREFIX + ".app";
    private static final String LOGGER_NAME = JaxRsDpsLogTest.class.getName();
    private static final String LOG_MESSAGE = "Hello World !";
    private static final List<String> LOG_MESSAGES = Arrays.asList("Hello", "world");
    private static final String MERGED_LOG_MESSAGE =
            "0: Hello" + System.lineSeparator() + "1: world" + System.lineSeparator();

    @Mock
    private ILogger logger;

    @Mock
    private DpsHeaders dpsHeaders;

    @Mock
    private AuditPayload auditPayload;

    @Mock
    private Request httpRequest;

    @Mock
    private Exception exception;

    private final Map<String, String> headers = new HashMap<>();

    @InjectMocks
    private JaxRsDpsLog jaxRsDpsLog;

    @Before
    public void setup() {
        final Field logPrefix = ReflectionUtils.findField(JaxRsDpsLog.class, "LOG_PREFIX");
        ReflectionUtils.makeAccessible(logPrefix);
        ReflectionUtils.setField(logPrefix, jaxRsDpsLog, LOG_PREFIX);
        when(dpsHeaders.getHeaders()).thenReturn(headers);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testAudit() {
        doNothing().when(logger).audit(eq(AUDIT_LOG_PREFIX), eq(auditPayload), eq(headers));
        jaxRsDpsLog.audit(auditPayload);
        verify(logger, times(1)).audit(eq(AUDIT_LOG_PREFIX), eq(auditPayload), eq(headers));
        verify(dpsHeaders, times(1)).getHeaders();
    }

    @Test
    public void testAuditWithLoggerName() {
        doNothing().when(logger).audit(eq(LOGGER_NAME), eq(AUDIT_LOG_PREFIX), eq(auditPayload), eq(headers));
        jaxRsDpsLog.audit(LOGGER_NAME, auditPayload);
        verify(logger, times(1)).audit(eq(LOGGER_NAME), eq(AUDIT_LOG_PREFIX), eq(auditPayload), eq(headers));
        verify(dpsHeaders, times(1)).getHeaders();
    }

    @Test
    public void testRequest() {
        doNothing().when(logger).request(eq(REQUEST_LOG_PREFIX), eq(httpRequest), eq(headers));
        jaxRsDpsLog.request(httpRequest);
        verify(logger, times(1)).request(eq(REQUEST_LOG_PREFIX), eq(httpRequest), eq(headers));
        verify(dpsHeaders, times(1)).getHeaders();
    }

    @Test
    public void testRequestWithLoggerName() {
        doNothing().when(logger).request(eq(LOGGER_NAME), eq(REQUEST_LOG_PREFIX), eq(httpRequest), eq(headers));
        jaxRsDpsLog.request(LOGGER_NAME, httpRequest);
        verify(logger, times(1)).request(eq(LOGGER_NAME), eq(REQUEST_LOG_PREFIX), eq(httpRequest), eq(headers));
        verify(dpsHeaders, times(1)).getHeaders();
    }

    @Test
    public void testInfo() {
        doNothing().when(logger).info(eq(APP_LOG_PREFIX), eq(LOG_MESSAGE), eq(headers));
        jaxRsDpsLog.info(LOG_MESSAGE);
        verify(logger, times(1)).info(eq(APP_LOG_PREFIX), eq(LOG_MESSAGE), eq(headers));
        verify(dpsHeaders, times(1)).getHeaders();
    }

    @Test
    public void testInfoWithLoggerName() {
        doNothing().when(logger).info(eq(LOGGER_NAME), eq(APP_LOG_PREFIX), eq(LOG_MESSAGE), eq(headers));
        jaxRsDpsLog.info(LOGGER_NAME, LOG_MESSAGE);
        verify(logger, times(1)).info(eq(LOGGER_NAME), eq(APP_LOG_PREFIX), eq(LOG_MESSAGE), eq(headers));
        verify(dpsHeaders, times(1)).getHeaders();
    }

    @Test
    public void testDebug() {
        doNothing().when(logger).debug(eq(APP_LOG_PREFIX), eq(LOG_MESSAGE), eq(headers));
        jaxRsDpsLog.debug(LOG_MESSAGE);
        verify(logger, times(1)).debug(eq(APP_LOG_PREFIX), eq(LOG_MESSAGE), eq(headers));
        verify(dpsHeaders, times(1)).getHeaders();
    }

    @Test
    public void testDebugWithLoggerName() {
        doNothing().when(logger).debug(eq(LOGGER_NAME), eq(APP_LOG_PREFIX), eq(LOG_MESSAGE), eq(headers));
        jaxRsDpsLog.debug(LOGGER_NAME, LOG_MESSAGE);
        verify(logger, times(1)).debug(eq(LOGGER_NAME), eq(APP_LOG_PREFIX), eq(LOG_MESSAGE), eq(headers));
        verify(dpsHeaders, times(1)).getHeaders();
    }

    @Test
    public void testDebugWithMessageList() {
        doNothing().when(logger).debug(eq(APP_LOG_PREFIX), eq(MERGED_LOG_MESSAGE), eq(headers));
        jaxRsDpsLog.debug(LOG_MESSAGES);
        verify(logger, times(1)).debug(eq(APP_LOG_PREFIX), eq(MERGED_LOG_MESSAGE), eq(headers));
        verify(dpsHeaders, times(1)).getHeaders();
    }

    @Test
    public void testWarning() {
        doNothing().when(logger).warning(eq(APP_LOG_PREFIX), eq(LOG_MESSAGE), eq(headers));
        jaxRsDpsLog.warning(LOG_MESSAGE);
        verify(logger, times(1)).warning(eq(APP_LOG_PREFIX), eq(LOG_MESSAGE), eq(headers));
        verify(dpsHeaders, times(1)).getHeaders();
    }

    @Test
    public void testWarningWithLoggerName() {
        doNothing().when(logger).warning(eq(LOGGER_NAME), eq(APP_LOG_PREFIX), eq(LOG_MESSAGE), eq(headers));
        jaxRsDpsLog.warning(LOGGER_NAME, LOG_MESSAGE);
        verify(logger, times(1)).warning(eq(LOGGER_NAME), eq(APP_LOG_PREFIX), eq(LOG_MESSAGE), eq(headers));
        verify(dpsHeaders, times(1)).getHeaders();
    }

    @Test
    public void testWarningWithMessageList() {
        doNothing().when(logger).warning(eq(APP_LOG_PREFIX), eq(MERGED_LOG_MESSAGE), eq(headers));
        jaxRsDpsLog.warning(LOG_MESSAGES);
        verify(logger, times(1)).warning(eq(APP_LOG_PREFIX), eq(MERGED_LOG_MESSAGE), eq(headers));
        verify(dpsHeaders, times(1)).getHeaders();
    }

    @Test
    public void testWarningWithMessageListAndLoggerName() {
        doNothing().when(logger).warning(eq(LOGGER_NAME), eq(APP_LOG_PREFIX), eq(MERGED_LOG_MESSAGE), eq(headers));
        jaxRsDpsLog.warning(LOGGER_NAME, LOG_MESSAGES);
        verify(logger, times(1)).warning(eq(LOGGER_NAME), eq(APP_LOG_PREFIX), eq(MERGED_LOG_MESSAGE), eq(headers));
        verify(dpsHeaders, times(1)).getHeaders();
    }

    @Test
    public void testWarningWithException() {
        doNothing().when(logger).warning(eq(APP_LOG_PREFIX), eq(LOG_MESSAGE), eq(exception), eq(headers));
        jaxRsDpsLog.warning(LOG_MESSAGE, exception);
        verify(logger, times(1)).warning(eq(APP_LOG_PREFIX), eq(LOG_MESSAGE), eq(exception), eq(headers));
        verify(dpsHeaders, times(1)).getHeaders();
    }

    @Test
    public void testWarningWithExceptionAndLoggerName() {
        doNothing().when(logger).warning(eq(LOGGER_NAME), eq(APP_LOG_PREFIX), eq(LOG_MESSAGE), eq(exception),
                eq(headers));
        jaxRsDpsLog.warning(LOGGER_NAME, LOG_MESSAGE, exception);
        verify(logger, times(1)).warning(eq(LOGGER_NAME), eq(APP_LOG_PREFIX), eq(LOG_MESSAGE),
                eq(exception), eq(headers));
        verify(dpsHeaders, times(1)).getHeaders();
    }

    @Test
    public void testError() {
        doNothing().when(logger).error(eq(APP_LOG_PREFIX), eq(LOG_MESSAGE), eq(headers));
        jaxRsDpsLog.error(LOG_MESSAGE);
        verify(logger, times(1)).error(eq(APP_LOG_PREFIX), eq(LOG_MESSAGE), eq(headers));
        verify(dpsHeaders, times(1)).getHeaders();
    }

    @Test
    public void testErrorWithLoggerName() {
        doNothing().when(logger).error(eq(LOGGER_NAME), eq(APP_LOG_PREFIX), eq(LOG_MESSAGE), eq(headers));
        jaxRsDpsLog.error(LOGGER_NAME, LOG_MESSAGE);
        verify(logger, times(1)).error(eq(LOGGER_NAME), eq(APP_LOG_PREFIX), eq(LOG_MESSAGE), eq(headers));
        verify(dpsHeaders, times(1)).getHeaders();
    }

    @Test
    public void testErrorWithException() {
        doNothing().when(logger).error(eq(APP_LOG_PREFIX), eq(LOG_MESSAGE), eq(exception), eq(headers));
        jaxRsDpsLog.error(LOG_MESSAGE, exception);
        verify(logger, times(1)).error(eq(APP_LOG_PREFIX), eq(LOG_MESSAGE), eq(exception), eq(headers));
        verify(dpsHeaders, times(1)).getHeaders();
    }

    @Test
    public void testErrorWithExceptionAndLoggerName() {
        doNothing().when(logger).error(eq(LOGGER_NAME), eq(APP_LOG_PREFIX), eq(LOG_MESSAGE), eq(exception),
                eq(headers));
        jaxRsDpsLog.error(LOGGER_NAME, LOG_MESSAGE, exception);
        verify(logger, times(1)).error(eq(LOGGER_NAME), eq(APP_LOG_PREFIX), eq(LOG_MESSAGE),
                eq(exception), eq(headers));
        verify(dpsHeaders, times(1)).getHeaders();
    }
}
