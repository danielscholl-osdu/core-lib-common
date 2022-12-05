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

package org.opengroup.osdu.core.common.http;

import com.google.common.base.Strings;
import org.opengroup.osdu.core.common.Constants;
import org.opengroup.osdu.core.common.model.http.CollaborationContext;
import org.opengroup.osdu.core.common.util.CollaborationContextUtil;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class CollaborationContextFactory implements ICollaborationFactory {

    @Override
    public Optional<CollaborationContext> create(String collaborationDirectives) {
        if(Strings.isNullOrEmpty(collaborationDirectives))
            return Optional.empty();
        Map<String, String> collaborationProperties = CollaborationContextUtil.getCollaborationDirectiveProperties(collaborationDirectives);
        return Optional.of(new CollaborationContext(UUID.fromString(collaborationProperties.get(Constants.ID)), collaborationProperties.get(Constants.APPLICATION), collaborationProperties));
    }
}
