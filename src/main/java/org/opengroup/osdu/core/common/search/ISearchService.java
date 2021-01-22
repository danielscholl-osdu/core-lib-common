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

package org.opengroup.osdu.core.common.search;

import com.google.gson.JsonObject;
import org.opengroup.osdu.core.common.model.search.*;

public interface ISearchService {
    CursorQueryResponse getAllKindEntries(String kind) throws SearchException;

    QueryResponse search(QueryRequest searchRequest) throws SearchException;

    CursorQueryResponse searchCursor(CursorQueryRequest cursorRequest) throws SearchException;

    JsonObject getIndexSchema(String kind) throws SearchException;

    void deleteIndex(String kind) throws SearchException;
}