package org.opengroup.osdu.core.common.search;

import org.opengroup.osdu.core.common.model.http.DpsHeaders;

public interface ISearchFactory {
    ISearchService create(DpsHeaders var1);
}