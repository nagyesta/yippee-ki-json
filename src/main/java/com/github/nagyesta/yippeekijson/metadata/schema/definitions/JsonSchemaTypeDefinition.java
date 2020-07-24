package com.github.nagyesta.yippeekijson.metadata.schema.definitions;

import org.jetbrains.annotations.Nullable;

/**
 * Adds type, comment and description fields to a {@link JsonSchemaObject}.
 */
public interface JsonSchemaTypeDefinition extends JsonSchemaObject {

    /**
     * Returns the type of the type definition.
     *
     * @return the type
     */
    @Nullable
    JsonSimpleType getType();

    /**
     * Returns the comment (e.g. wiki link) related to the type definition.
     *
     * @return the $comment
     */
    @Nullable
    String getComment();

    /**
     * Returns the description of the type definition.
     *
     * @return the description
     */
    @Nullable
    String getDescription();
}
