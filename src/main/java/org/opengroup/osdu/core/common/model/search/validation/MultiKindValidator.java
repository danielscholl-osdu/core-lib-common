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
import org.opengroup.osdu.core.common.model.validation.ValidatorUtils;
import org.opengroup.osdu.core.common.search.ElasticIndexNameResolver;
import org.opengroup.osdu.core.common.util.KindParser;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class MultiKindValidator implements ConstraintValidator<ValidMultiKind, Object> {

    private static final String MULTI_KIND_PATTERN = "[\\w-\\.\\*]+:[\\w-\\.\\*]+:[\\w-\\.\\*]+:[(\\d+.)+(\\d+.)+(\\d+)\\*]+$";
    private final ElasticIndexNameResolver elasticIndexNameResolver = new ElasticIndexNameResolver();

    // ElasticSearch sets the index names (that are transformed kind names) in the URI. Max. length of a URI is 4096.
    // Assuming max. length of the rest parts in a URI is 256, then MAX_KIND_LENGTH = 4096 - 256
    private static final int MAX_KIND_LENGTH = 3840;

    @Override
    public void initialize(ValidMultiKind constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object kind, ConstraintValidatorContext context) {
        try {
            List<String> kinds = KindParser.parse(kind);
            if (kinds.size() == 0) {
                addConstraintViolation(SwaggerDoc.KIND_VALIDATION_CAN_NOT_BE_NULL_OR_EMPTY, kind, context);
                return false;
            }

            int totalLen = 0;
            for (String singleKind : kinds) {
                if (!singleKind.matches(MULTI_KIND_PATTERN)) {
                    addConstraintViolation(SwaggerDoc.KIND_VALIDATION_NOT_SUPPORTED_FORMAT, kind, context);
                    return false;
                }
                if(elasticIndexNameResolver.isIndexAliasSupported(singleKind)) {
                    // The length of the alias starting with 'a' is not more than 11 characters
                    totalLen += elasticIndexNameResolver.getIndexAliasFromKind(singleKind).length();
                }
                else {
                    // The length of the kind is about 45 characters on average
                    totalLen += singleKind.length();
                }
                totalLen += 1; //1: length of the separate ','
            }

            if (totalLen > MAX_KIND_LENGTH) {
                String msg = String.format(SwaggerDoc.KIND_VALIDATION_EXCEED_MAX_LENGTH, MAX_KIND_LENGTH);
                addConstraintViolation(msg, kind, context);
                return false;
            }

            return true;
        } catch (IllegalArgumentException ex) {
            addConstraintViolation(ex.getMessage(), kind, context);
            return false;
        }
    }

    private void addConstraintViolation(String message, Object kind, ConstraintValidatorContext context) {
        if (context != null) {
            String msg = message + ". Found: " + kind;
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ValidatorUtils.escapeString(msg)).addConstraintViolation();
        }
    }
}