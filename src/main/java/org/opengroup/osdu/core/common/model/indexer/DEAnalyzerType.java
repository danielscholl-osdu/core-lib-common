package org.opengroup.osdu.core.common.model.indexer;

public enum DEAnalyzerType {

    INDEXER_ANALYZER("de_indexer_analyzer"),

    SEARCH_ANALYZER("de_search_analyzer");

    private final String value;

    DEAnalyzerType(String value) { this.value = value; }

    public String getValue() { return this.value; }
}
