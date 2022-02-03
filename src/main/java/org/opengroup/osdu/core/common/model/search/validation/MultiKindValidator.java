
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

package org.opengroup.osdu.core.common.model.search.validation;

import org.opengroup.osdu.core.common.SwaggerDoc;
import org.opengroup.osdu.core.common.util.KindParser;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class MultiKindValidator implements ConstraintValidator<ValidMultiKind, Object> {

    //tenant1:ihs:well:1.0.0
    private static final String MULTI_KIND_PATTERN = "[\\w-\\.\\*]+:[\\w-\\.\\*]+:[\\w-\\.\\*]+:[(\\d+.)+(\\d+.)+(\\d+)\\*]+$";
    // ElasticSearch sets the index names (that are transformed kind names) in the URI. Max. length of a URI is 4096.
    // Assuming max. length of the rest parts in a URI is 256, then MAX_KIND_LENGTH = 4096 - 256
    private static final int Max_KIND_LENGTH = 3840;

    @Override
    public void initialize(ValidMultiKind constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object kind, ConstraintValidatorContext context) {
        try {
            List<String> kinds = KindParser.parse(kind);
            if(kinds.size() == 0) {
                context.buildConstraintViolationWithTemplate(SwaggerDoc.KIND_VALIDATION_CAN_NOT_BE_NULL_OR_EMPTY).addConstraintViolation();
                return false;
            }

            int totalLen = 0;
            for(int i = 0; i < kinds.size(); i++)
            {
                String singleKind = kinds.get(i);
                if(!singleKind.matches(MULTI_KIND_PATTERN)) {
                    context.buildConstraintViolationWithTemplate(SwaggerDoc.KIND_VALIDATION_Not_SUPPORTED_FORMAT).addConstraintViolation();
                    return false;
                }
                totalLen += singleKind.length() + 1; //1: length of the separate ','
            }

            if(totalLen > Max_KIND_LENGTH) {
                String msg = String.format(SwaggerDoc.KIND_VALIDATION_EXCEED_MAX_LENGTH, Max_KIND_LENGTH);
                context.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
                return false;
            }

            return true;
        }
        catch (IllegalArgumentException ex) {
            context.buildConstraintViolationWithTemplate(ex.getMessage()).addConstraintViolation();
            return false;
        }
    }
}