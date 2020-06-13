package org.opengroup.osdu.core.common.model.legal.jobs;

public class ComplianceUpdateStoppedException extends Exception {
    public ComplianceUpdateStoppedException(long runningTimeSeconds){
        super(String.format("Compliance job has been stopped as it was running for longer than %s seconds", runningTimeSeconds));
    }
}
