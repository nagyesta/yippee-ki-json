package com.github.nagyesta.yippeekijson.metadata.schema.definitions;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Single source of all registered type definitions.
 */
public interface CompositeTypeDefinitionRegistry {

    /**
     * Resolves a known common type to the registered type reference String.
     *
     * @param commonType the registered type.
     * @return The resolved String (empty if the type is not known)
     */
    @NotNull
    Optional<String> toCommonTypeReference(@NotNull Type commonType);

    /**
     * Returns the generic rule definition.
     *
     * @return type definition representing any rule
     */
    @NotNull
    Optional<String> anyRuleTypeReference();

    /**
     * Resolves a suitable collection of predicate types based on the input it will need to evaluate.
     *
     * @param inputType the type of input that needs to be supported by the predicate.
     * @return The resolved String (empty if the type is not known)
     */
    @NotNull
    Optional<String> toPredicateTypeReference(@NotNull Type inputType);

    /**
     * Resolves a suitable collection of supplier types based on the output it will need to generate.
     *
     * @param outputType the type of output that needs to be supported by the supplier.
     * @return The resolved String (empty if the type is not known)
     */
    @NotNull
    Optional<String> toSupplierTypeReference(@NotNull Type outputType);

    /**
     * Resolves a suitable collection of function types based on the input and output it will need to support.
     *
     * @param inputType  the type of input that needs to be supported by the function.
     * @param outputType the type of output that needs to be supported by the function.
     * @return The resolved String (empty if the type is not known)
     */
    @NotNull
    Optional<String> toFunctionTypeReference(@NotNull Type inputType, @NotNull Type outputType);

}
