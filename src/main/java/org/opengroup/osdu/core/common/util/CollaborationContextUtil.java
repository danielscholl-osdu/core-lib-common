// Copyright 2022 Schlumberger
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

package org.opengroup.osdu.core.common.util;

import lombok.NonNull;
import org.opengroup.osdu.core.common.model.http.CollaborationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CollaborationContextUtil {

    public static Map<String, String> getCollaborationDirectiveProperties(String collaborationDirectives) {
        Map<String, String> collaborationDirectiveProperties = new HashMap<>();
        String[] directives = collaborationDirectives.split(",");
        for (String directive : directives) {
            String[] keyValue = directive.split("=");
            collaborationDirectiveProperties.put(keyValue[0].toLowerCase().trim(), keyValue[1].trim());
        }
        return collaborationDirectiveProperties;
    }

    public static String getNamespace(@NonNull Optional<CollaborationContext> collaborationContext) {
        return collaborationContext.map(CollaborationContext::getId).orElse("");
    }

    public static String composeIdWithNamespace(String id, @NonNull Optional<CollaborationContext> collaborationContext) {
        return getNamespace(collaborationContext) + id;
    }

    public static String getIdWithoutNamespace(String recordId, Optional<CollaborationContext> collaborationContext) {
        return collaborationContext
                .map(CollaborationContext::getId).map(String::length).map(recordId::substring)
                .orElse(recordId);
    }

}
