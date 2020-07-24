package com.github.nagyesta.yippeekijson.metadata.schema.definitions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

/**
 * Registry for type definitions of named components.
 * Supports on of the following types:
 * <ul>
 *     <li>{@link com.github.nagyesta.yippeekijson.core.rule.JsonRule}</li>
 *     <li>{@link java.util.function.Supplier}</li>
 *     <li>{@link java.util.function.Function}</li>
 *     <li>{@link java.util.function.Predicate}</li>
 * </ul>
 *
 * @param <T> the type of the named component
 */
public interface JsonNamedComponentTypeDefinitionRegistry<T> {

    /**
     * Type definition group name for rules.
     */
    String RULE_TYPES = "ruleTypes";
    /**
     * Type definition group name for predicates.
     */
    String PREDICATE_TYPES = "predicateTypes";
    /**
     * Type definition group name for suppliers.
     */
    String SUPPLIER_TYPES = "supplierTypes";
    /**
     * Type definition group name for functions.
     */
    String FUNCTION_TYPES = "functionTypes";

    /**
     * Returns all known named type definitions.
     * The keys are the property names (last part of the type reference), while
     * the values are the parsed named types.
     *
     * @return map of definitions
     */
    @NotNull
    Map<String, JsonSchemaTypeDefinition> registeredDefinitions();

    /**
     * Resolves a String referencing group of known named types which support the given input/output types.
     *
     * @param inputType  the input type consumed by the elements of the group. Used for Predicate and Function.
     * @param outputType the output type produced by the elements of the group. Used for Supplier and Function.
     * @return The resolved String (empty if the type is not known)
     */
    @NotNull
    Optional<String> toTypeReference(@Nullable Type inputType, @Nullable Type outputType);

    /**
     * Registers a common type using the provided type.
     *
     * @param namedType The type we will use to resolve the reference later.
     */
    void registerType(@NotNull Class<? extends T> namedType);


}
