package com.github.nagyesta.yippeekijson.metadata.schema.definitions.registry;

import com.github.nagyesta.yippeekijson.core.rule.JsonRule;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.CompositeTypeDefinitionRegistry;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonCommonTypeDefinitionRegistry;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonNamedComponentTypeDefinitionRegistry;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class CompositeTypeDefinitionRegistryImpl implements CompositeTypeDefinitionRegistry {

    private final JsonCommonTypeDefinitionRegistry commonTypeDefinitionRegistry;
    private final JsonNamedComponentTypeDefinitionRegistry<Supplier<?>> supplierTypeDefinitionRegistry;
    private final JsonNamedComponentTypeDefinitionRegistry<Predicate<?>> predicateTypeDefinitionRegistry;
    private final JsonNamedComponentTypeDefinitionRegistry<Function<?, ?>> functionTypeDefinitionRegistry;
    private final JsonNamedComponentTypeDefinitionRegistry<JsonRule> ruleTypeDefinitionRegistry;

    public CompositeTypeDefinitionRegistryImpl(
            final JsonCommonTypeDefinitionRegistry commonTypeDefinitionRegistry,
            final JsonNamedComponentTypeDefinitionRegistry<Supplier<?>> supplierTypeDefinitionRegistry,
            final JsonNamedComponentTypeDefinitionRegistry<Predicate<?>> predicateTypeDefinitionRegistry,
            final JsonNamedComponentTypeDefinitionRegistry<Function<?, ?>> functionTypeDefinitionRegistry,
            final JsonNamedComponentTypeDefinitionRegistry<JsonRule> ruleTypeDefinitionRegistry) {
        this.commonTypeDefinitionRegistry = commonTypeDefinitionRegistry;
        this.supplierTypeDefinitionRegistry = supplierTypeDefinitionRegistry;
        this.predicateTypeDefinitionRegistry = predicateTypeDefinitionRegistry;
        this.functionTypeDefinitionRegistry = functionTypeDefinitionRegistry;
        this.ruleTypeDefinitionRegistry = ruleTypeDefinitionRegistry;
    }

    @Override
    @NotNull
    public Optional<String> toCommonTypeReference(@NotNull final Type commonType) {
        return this.commonTypeDefinitionRegistry.toTypeReference(commonType);
    }

    @Override
    @NotNull
    public Optional<String> anyRuleTypeReference() {
        return this.ruleTypeDefinitionRegistry.toTypeReference(null, null);
    }

    @Override
    @NotNull
    public Optional<String> toPredicateTypeReference(@NotNull final Type inputType) {
        return this.predicateTypeDefinitionRegistry.toTypeReference(inputType, null);
    }

    @Override
    @NotNull
    public Optional<String> toSupplierTypeReference(@NotNull final Type outputType) {
        return this.supplierTypeDefinitionRegistry.toTypeReference(null, outputType);
    }

    @Override
    @NotNull
    public Optional<String> toFunctionTypeReference(@NotNull final Type inputType, @NotNull final Type outputType) {
        return this.functionTypeDefinitionRegistry.toTypeReference(inputType, outputType);
    }

}
