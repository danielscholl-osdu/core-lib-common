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