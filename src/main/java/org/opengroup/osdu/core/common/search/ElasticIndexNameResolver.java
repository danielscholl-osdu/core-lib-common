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

import com.google.api.client.util.Strings;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * get elastic index name for the kind and cache the mapping kind <-> index-name
 *
 * elastic index name has following restrictions
 *  - must not contain the #, \, /, *, ?, ", <, >, |, :
 *  - must not start with _, - or +
 *  - must not be . or ..
 *  - must be lowercase
 *  restriction can be found here:
 *  https://github.com/elastic/elasticsearch/blob/870a913217be6de41c4f0a91c9fa5493017c8554/server/src/test/java/org/elasticsearch/cluster/metadata/MetaDataCreateIndexServiceTests.java#L400
 * */
@Component
public class ElasticIndexNameResolver {
    private static final String KIND_COMPLETE_VERSION_PATTERN = "[\\w-\\.\\*]+:[\\w-\\.\\*]+:[\\w-\\.\\*]+:(\\d+\\.\\d+\\.\\d+)$";
    private static final String KIND_MAJOR_VERSION_PATTERN = "[\\w-\\.\\*]+:[\\w-\\.\\*]+:[\\w-\\.\\*]+:(\\d+\\.\\*\\.\\*)$";

    private final Map<String, String> KIND_INDEX_MAP = new ConcurrentHashMap();
    private final Map<String, String> INDEX_KIND_MAP = new ConcurrentHashMap();


    public String getIndexNameFromKind(String kind) {

        String index = kind.replace(":", "-").toLowerCase();
        if (KIND_INDEX_MAP.containsKey(kind)) {
            return KIND_INDEX_MAP.get(kind);
        }
        else {
            KIND_INDEX_MAP.putIfAbsent(kind, index);
            INDEX_KIND_MAP.putIfAbsent(index, kind);
        }

        return KIND_INDEX_MAP.get(kind);
    }

    public String getKindFromIndexName(String indexName) {

        if (INDEX_KIND_MAP.containsKey(indexName)) {
            return INDEX_KIND_MAP.get(indexName);
        }

        return indexName.replace("-", ":").toLowerCase();
    }

    /**
     *
     * @param kind a kind with valid format
     * @return true if index name alias is supported for the given kind; otherwise, returns false
     */
    public boolean isIndexAliasSupported(String kind) {
        return !Strings.isNullOrEmpty(kind) && (kind.matches(KIND_COMPLETE_VERSION_PATTERN) || kind.matches(KIND_MAJOR_VERSION_PATTERN));
    }

    /**
     *
     * @param kind a kind with valid format
     * @return a string started with 'a' if index name alias is supported for the given kind; otherwise, returns null
     */
    public String getIndexAliasFromKind(String kind) {
        if(isIndexAliasSupported(kind)) {
            String indexName = getIndexNameFromKind(kind);
            return String.format("a%d", indexName.hashCode());
        }

        return null;
    }

}