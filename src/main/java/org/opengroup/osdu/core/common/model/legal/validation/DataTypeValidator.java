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

package org.opengroup.osdu.core.common.model.legal.validation;

import org.opengroup.osdu.core.common.model.legal.DataTypeValues;
import org.opengroup.osdu.core.common.model.http.RequestInfo;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DataTypeValidator implements ConstraintValidator<ValidDataType, String> {

    private RequestInfo requestInfo;
    private DataTypeValues dataTypeValues = new DataTypeValues();

    @Inject
    public DataTypeValidator(RequestInfo requestInfo){
        this.requestInfo = requestInfo;
    }

    @Override
    public void initialize(ValidDataType constraintAnnotation) {
        //needed by interface - we don't use
    }

    @Override
    public boolean isValid(String dataType, ConstraintValidatorContext context) {
        String ruleSet = requestInfo.getComplianceRuleSet();
        return dataType == null ? false :
                dataTypeValues.getDataTypeValues(ruleSet).stream().anyMatch(dataType::equalsIgnoreCase);
    }

    //to enable integration tests
    public void setRequestInfo(RequestInfo requestInfo){
        this.requestInfo = requestInfo;
    }
}
