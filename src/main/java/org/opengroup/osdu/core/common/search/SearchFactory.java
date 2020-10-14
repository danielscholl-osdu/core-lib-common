package org.opengroup.osdu.core.common.search;

import org.opengroup.osdu.core.common.http.HttpClient;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;

public class SearchFactory implements ISearchFactory {

    private final SearchAPIConfig config;

    public SearchFactory(SearchAPIConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("SearchAPIConfig cannot be empty");
        }
        this.config = config;
    }

    @Override
    public ISearchService create(DpsHeaders headers) {
        if (headers == null) {
            throw new NullPointerException("headers cannot be null");
        }
        return new SearchService(this.config, new HttpClient(), headers);
    }
}
