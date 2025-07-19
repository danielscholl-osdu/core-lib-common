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

package org.opengroup.osdu.core.common.search;

import com.google.common.base.Strings;
import org.opengroup.osdu.core.common.model.search.DeploymentEnvironment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.PatternSyntaxException;

@Component
public class Config {

    @Value("${QUERY_DEFAULT_LIMIT}")
    private static String QUERY_DEFAULT_LIMIT;

    @Value("${QUERY_LIMIT_MAXIMUM}")
    private static String QUERY_LIMIT_MAXIMUM;

    public static int getQueryDefaultLimit() {
        String queryDefaultLimit = !Strings.isNullOrEmpty(QUERY_DEFAULT_LIMIT) ? QUERY_DEFAULT_LIMIT : getEnvironmentVariable("QUERY_DEFAULT_LIMIT");
        return Integer.parseInt(getDefaultOrEnvironmentValue(queryDefaultLimit, 10));
    }

    public static int getQueryLimitMaximum() {
        String queryLimitMaximum = !Strings.isNullOrEmpty(QUERY_LIMIT_MAXIMUM) ? QUERY_LIMIT_MAXIMUM : getEnvironmentVariable("QUERY_LIMIT_MAXIMUM");
        return Integer.parseInt(getDefaultOrEnvironmentValue(queryLimitMaximum, 1000));
    }

    private static <T> String getDefaultOrEnvironmentValue(T givenValue, T defaultValue) {
        if (givenValue == null || Strings.isNullOrEmpty(givenValue.toString())) {
            return defaultValue.toString();
        }
        return givenValue.toString();
    }

    private static String getEnvironmentVariable(String propertyKey) {
        return System.getProperty(propertyKey, System.getenv(propertyKey));
    }

    private static Config instance = new Config();

    public static Config Instance() {
        return instance;
    }
}
