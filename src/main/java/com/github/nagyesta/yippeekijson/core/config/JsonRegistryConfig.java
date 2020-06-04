package com.github.nagyesta.yippeekijson.core.config;

import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonRuleRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.impl.FunctionRegistryImpl;
import com.github.nagyesta.yippeekijson.core.config.parser.impl.JsonMapperImpl;
import com.github.nagyesta.yippeekijson.core.config.parser.impl.JsonRuleRegistryImpl;
import com.github.nagyesta.yippeekijson.core.function.*;
import com.github.nagyesta.yippeekijson.core.predicate.*;
import com.github.nagyesta.yippeekijson.core.rule.JsonRule;
import com.github.nagyesta.yippeekijson.core.rule.impl.*;
import com.github.nagyesta.yippeekijson.core.supplier.StaticJsonSupplier;
import com.github.nagyesta.yippeekijson.core.supplier.StaticStringSupplier;
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
        return List.of(StaticJsonSupplier.class,
                StaticStringSupplier.class);
    }

    @Bean
    public List<Class<? extends Function<?, ?>>> autoRegisterFunctions() {
        return List.of(ChangeCaseFunction.class,
                CloneKeyFunction.class,
                DecimalAddFunction.class,
                DecimalDivideFunction.class,
                DecimalMultiplyFunction.class,
                DecimalSubtractFunction.class,
                EpochMilliDateAddFunction.class,
                LiteralReplaceFunction.class,
                RegexReplaceFunction.class,
                StringDateAddFunction.class);
    }

    @Bean
    public List<Class<? extends Predicate<Object>>> autoRegisterPredicates() {
        return List.of(AllMatchPredicate.class,
                AnyMatchPredicate.class,
                AnyStringPredicate.class,
                ContainsKeyPredicate.class,
                EvalOnPredicate.class,
                IsNullPredicate.class,
                NoneMatchPredicate.class,
                NotNullPredicate.class,
                RegexPredicate.class,
                SpringExpressionLanguagePredicate.class);
    }

    @Bean
    public JsonMapper yippeeJsonMapper() {
        return new JsonMapperImpl();
    }

    @Bean
    public FunctionRegistry functionRegistry() {
        return new FunctionRegistryImpl(yippeeJsonMapper(), autoRegisterSuppliers(), autoRegisterFunctions(), autoRegisterPredicates());
    }

    @Bean
    public List<Class<? extends JsonRule>> autoRegisterRules() {
        return List.of(JsonAddRule.class,
                JsonCalculateRule.class,
                JsonCopyRule.class,
                JsonDeleteFromMapRule.class,
                JsonDeleteRule.class,
                JsonRenameRule.class,
                JsonReplaceMapRule.class,
                JsonReplaceRule.class);
    }

    @Bean
    public JsonRuleRegistry jsonRuleRegistry() {
        return new JsonRuleRegistryImpl(functionRegistry(), autoRegisterRules());
    }
}
