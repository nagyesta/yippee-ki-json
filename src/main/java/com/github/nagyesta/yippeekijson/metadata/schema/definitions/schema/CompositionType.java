package com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema;

/**
 * Defines the type of composition we are using.
 */
public enum CompositionType {
    /**
     * anyOf.
     */
    ANY_OF("anyOf"),
    /**
     * allOf.
     */
    ALL_OF("allOf"),
    /**
     * oneOf.
     */
    ONE_OF("oneOf"),
    /**
     * not.
     */
    NOT("not");

    private final String field;

    CompositionType(final String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
