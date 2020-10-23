package org.opengroup.osdu.core.common.search;

import org.opengroup.osdu.core.common.model.http.DpsHeaders;

import org.junit.Test;

import static org.junit.Assert.*;

public class SearchFactoryTest {
    @Test
    public void constructorTest_when_non_null_config_should_not_throw_exception() {
        SearchAPIConfig config = new SearchAPIConfig("any url", "any key");
        SearchFactory searchFactory = new SearchFactory(config, null);
        assertNotNull(searchFactory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorTest_when_null_config_should_throw_exception() {
        SearchFactory searchFactory = new SearchFactory(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void createSearchService_instance_when_null_dps_headers_should_throw_exception() {
        SearchAPIConfig config = new SearchAPIConfig("any url", "any key");
        SearchFactory searchFactory = new SearchFactory(config, null);
        assertNotNull(searchFactory);
        searchFactory.create(null);
    }

    @Test
    public void createSearchService_instance_when_not_null_dps_headers_should_not_throw_exception() {
        SearchAPIConfig config = new SearchAPIConfig("any url", "any key");
        SearchFactory searchFactory = new SearchFactory(config, null);
        assertNotNull(searchFactory);
        ISearchService searchService = searchFactory.create(new DpsHeaders());
        assertNotNull(searchService);
    }
}