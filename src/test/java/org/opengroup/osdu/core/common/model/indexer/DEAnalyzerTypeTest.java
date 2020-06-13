package org.opengroup.osdu.core.common.model.indexer;

import org.junit.Assert;
import org.junit.Test;

public class DEAnalyzerTypeTest {

    @Test
    public void should_returnCorrectValue_getValueTest() {
        Assert.assertEquals("de_indexer_analyzer", DEAnalyzerType.INDEXER_ANALYZER.getValue());
        Assert.assertEquals("de_search_analyzer", DEAnalyzerType.SEARCH_ANALYZER.getValue());
    }
}