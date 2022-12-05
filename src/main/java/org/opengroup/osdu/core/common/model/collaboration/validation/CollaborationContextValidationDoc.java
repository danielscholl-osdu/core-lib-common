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

package org.opengroup.osdu.core.common.model.collaboration.validation;

public class CollaborationContextValidationDoc {
    public static final String COLLABORATION_ID_PATTERN = "^[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}$";
    public static final String COLLABORATION_APPLICATION_PATTERN = "^[a-zA-Z0-9 ]{1,128}$";
    public static final String DIRECTIVE_KEY_PATTERN = "^[a-zA-Z0-9_-]*$";
    public static final String DIRECTIVE_KEY_ALLOWED_CHARACTERS = "directives names can only contain alphanumerics, '-' and '_'";
    public static final String COLLABORATION_CONTEXT_EMPTY = "collaboration context cannot be empty";
    public static final String COLLABORATION_CONTEXT_MANDATORY_DIRECTIVES = "collaboration context must contain 'id' and 'application' directives";
    public static final String INVALID_ID_DIRECTIVE = "invalid directive 'id': '%s'. 'id' must be a valid GUID/UUID";
    public static final String INVALID_APPLICATION_DIRECTIVE = "invalid directive 'application': '%s'. 'application' must contain only alphanumerics and spaces and should not exceed 128 characters";
    public static final String DIRECTIVE_FORMAT = "all directives must have non-empty <key>=<value> format";
}
