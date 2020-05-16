package com.github.nagyesta.yippeekijson.core.config;

import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonRuleRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.impl.FunctionRegistryImpl;
import com.github.nagyesta.yippeekijson.core.config.parser.impl.JsonRuleRegistryImpl;
import com.github.nagyesta.yippeekijson.core.function.AnyStringPredicate;
import com.github.nagyesta.yippeekijson.core.function.RegexReplaceFunction;
import com.github.nagyesta.yippeekijson.core.function.StaticStringSupplier;
import com.github.nagyesta.yippeekijson.core.rule.JsonRule;
import com.github.nagyesta.yippeekijson.core.rule.impl.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Configuration
public class JsonRegistryConfig {

    @Bean
    public List<Class<? extends Supplier<?>>> autoRegisterSuppliers() {
        return List.of(StaticStringSupplier.class);
    }

    @Bean
    public List<Class<? extends Function<?, ?>>> autoRegisterFunctions() {
        return List.of(RegexReplaceFunction.class);
    }

    @Bean
    public List<Class<? extends Predicate<?>>> autoRegisterPredicates() {
        return List.of(AnyStringPredicate.class);
    }

    @Bean
    public FunctionRegistry functionRegistry() {
        return new FunctionRegistryImpl(autoRegisterSuppliers(), autoRegisterFunctions(), autoRegisterPredicates());
    }

    @Bean
    public List<Class<? extends JsonRule>> autoRegisterRules() {
        return List.of(JsonDeleteRule.class, JsonAddRule.class, JsonRenameRule.class, JsonReplaceRule.class, JsonCopyRule.class);
    }

    @Bean
    public JsonRuleRegistry jsonRuleRegistry() {
        return new JsonRuleRegistryImpl(functionRegistry(), autoRegisterRules());
    }
}
