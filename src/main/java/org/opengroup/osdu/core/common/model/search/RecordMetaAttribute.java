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

package org.opengroup.osdu.core.common.model.search;

/**
 * record meta attributes tags
 */
public enum RecordMetaAttribute {

    ID("id"),

    COLLABORATION_ID("collaborationId"),

    KIND("kind"),

    AUTHORITY("authority"),

    SOURCE("source"),

    NAMESPACE("namespace"),

    TYPE("type"),

    VERSION("version"),

    ACL("acl"),

    X_ACL("x-acl"),

    TAGS("tags"),

    LEGAL("legal"),

    ANCESTRY("ancestry"),

    CREATE_USER("createUser"),

    MODIFY_USER("modifyUser"),

    CREATE_TIME("createTime"),

    MODIFY_TIME("modifyTime"),

    BAG_OF_WORDS("bagOfWords"),

    INDEX_STATUS("index");

    private final String value;

    RecordMetaAttribute(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}