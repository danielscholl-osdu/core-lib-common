package org.opengroup.osdu.core.common.search;

import org.opengroup.osdu.core.common.http.HttpClient;
import org.opengroup.osdu.core.common.http.json.HttpResponseBodyMapper;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;

public class SearchFactory implements ISearchFactory {

    private final SearchAPIConfig config;
    private final HttpResponseBodyMapper bodyMapper;

    public SearchFactory(SearchAPIConfig config, HttpResponseBodyMapper bodyMapper) {
        if (config == null) {
            throw new IllegalArgumentException("SearchAPIConfig cannot be empty");
        }
        this.config = config;
        this.bodyMapper = bodyMapper;
    }

    @Override
    public ISearchService create(DpsHeaders headers) {
        if (headers == null) {
            throw new NullPointerException("headers cannot be null");
        }
        return new SearchService(this.config, new HttpClient(), headers, bodyMapper);
    }
}
