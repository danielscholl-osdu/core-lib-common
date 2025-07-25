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

import com.google.common.base.Throwables;
import org.opengroup.osdu.core.common.model.http.HeadersToLog;
import org.opengroup.osdu.core.common.model.http.Request;
import org.opengroup.osdu.core.common.logging.audit.AuditPayload;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;

@Component
public class DefaultLogger implements ILogger {

    private ILogWriter logWriter;
    private HeadersToLog headersToLog;

    public DefaultLogger() {
        logWriter = new DefaultLogWriter();
        this.headersToLog = new HeadersToLog(Collections.emptyList());
    }
    public DefaultLogger(HeadersToLog headersToLog, ILogWriter logWriter) {
        if(headersToLog == null)
            throw new IllegalArgumentException("Null HeadersToLog provided");
        if(logWriter == null)
            throw new IllegalArgumentException("Null Logger provided");
        this.logWriter = logWriter;
        this.headersToLog = headersToLog;
    }

    @Override
    public void audit(String logPrefix, AuditPayload payload, Map<String, String> headers){
        logWriter.writeJsonEntry(logPrefix, payload, headersToLog.createStandardLabelsFromMap(headers));
    }
    @Override
    public void request(String logPrefix, Request request, Map<String, String> labels){
        logWriter.writeRequestEntry(logPrefix,"#RequestLog", request, headersToLog.createStandardLabelsFromMap(labels));
    }
    @Override
    public void info(String logPrefix, String message, Map<String, String> labels){
        logWriter.writeEntry(logPrefix, Level.INFO, message, headersToLog.createStandardLabelsFromMap(labels));
    }

    @Override
    public void debug(String logPrefix, String message, Map<String, String> labels) {
        logWriter.writeEntry(logPrefix, Level.FINE, message, headersToLog.createStandardLabelsFromMap(labels));
    }

    @Override
    public void warning(String logPrefix, String message, Map<String, String> labels){
        logWriter.writeEntry(logPrefix, Level.WARNING, message, headersToLog.createStandardLabelsFromMap(labels));
    }
    @Override
    public void warning(String logPrefix, String message, Exception ex, Map<String, String> labels){
        String exString = Throwables.getStackTraceAsString(ex);
        logWriter.writeEntry(logPrefix, Level.WARNING, String.format("%s\n%s", message, exString), headersToLog.createStandardLabelsFromMap(labels));
    }
    @Override
    public void error(String logPrefix, String message, Map<String, String> labels){
        logWriter.writeEntry(logPrefix, Level.SEVERE, message, headersToLog.createStandardLabelsFromMap(labels));
    }
    @Override
    public void error(String logPrefix, String message, Exception ex, Map<String, String> labels){
        String exString = Throwables.getStackTraceAsString(ex);
        logWriter.writeEntry(logPrefix, Level.SEVERE, String.format("%s\n%s", message, exString), headersToLog.createStandardLabelsFromMap(labels));
    }
    @Override
    public void close() throws Exception {
        if(logWriter != null) {
            logWriter.close();
        }
    }
}
