package org.opengroup.osdu.core.common.model.indexer;

public enum DeletionType {
    /**
     * A soft deletion operation
     */
    soft("soft"),

    /**
     * A hard deletion operation
     */
    hard("hard");

    private final String value;

    DeletionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}