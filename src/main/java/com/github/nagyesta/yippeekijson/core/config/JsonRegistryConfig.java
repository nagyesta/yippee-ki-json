package com.github.nagyesta.yippeekijson.core.config;

import com.github.nagyesta.yippeekijson.core.annotation.Injectable;
import com.github.nagyesta.yippeekijson.core.config.entities.HttpConfig;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonRuleRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.impl.FunctionRegistryImpl;
import com.github.nagyesta.yippeekijson.core.config.parser.impl.JsonMapperImpl;
import com.github.nagyesta.yippeekijson.core.config.parser.impl.JsonRuleRegistryImpl;
import com.github.nagyesta.yippeekijson.core.function.*;
import com.github.nagyesta.yippeekijson.core.http.HttpClient;
import com.github.nagyesta.yippeekijson.core.http.impl.DefaultHttpClient;
import com.github.nagyesta.yippeekijson.core.predicate.*;
import com.github.nagyesta.yippeekijson.core.rule.JsonRule;
import com.github.nagyesta.yippeekijson.core.rule.impl.*;
import com.github.nagyesta.yippeekijson.core.supplier.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Configuration
public class JsonRegistryConfig {

    @Bean
    public ConversionService conversionService() {
        ConversionServiceFactoryBean bean = new ConversionServiceFactoryBean();
        bean.afterPropertiesSet();
        return bean.getObject();
    }

    @Bean
    public List<Class<? extends Supplier<?>>> autoRegisterSuppliers() {
        return List.of(
                ConvertingSupplier.class,
                FileContentSupplier.class,
                HttpResourceContentSupplier.class,
                JsonSchemaSupplier.class,
                SchemaStoreSchemaContentSupplier.class,
                StaticJsonSupplier.class,
                StaticStringSupplier.class,
                StaticIntegerSupplier.class,
                StaticDecimalSupplier.class,
                StaticBooleanSupplier.class,
                EpocMillisRelativeDateSupplier.class,
                RelativeStringDateSupplier.class);
    }

    @Bean
    public List<Class<? extends Function<?, ?>>> autoRegisterFunctions() {
        return List.of(
                ChangeCaseFunction.class,
                CloneKeyFunction.class,
                DecimalAddFunction.class,
                AbsoluteValueFunction.class,
                DecimalDivideFunction.class,
                DecimalDividendFunction.class,
                DecimalMultiplyFunction.class,
                DecimalSubtractFunction.class,
                DecimalSubtractFromFunction.class,
                RoundDecimalFunction.class,
                EpochMilliDateAddFunction.class,
                HttpResourceContentFunction.class,
                HttpResourceContentMapFunction.class,
                JsonParseFunction.class,
                LiteralReplaceFunction.class,
                RegexReplaceFunction.class,
                StringDateAddFunction.class);
    }

    @Bean
    public List<Class<? extends Predicate<Object>>> autoRegisterPredicates() {
        return List.of(
                AllMatchPredicate.class,
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
    @Injectable(forType = JsonMapper.class)
    public JsonMapper yippeeJsonMapper() {
        return new JsonMapperImpl();
    }

    @Bean
    @Injectable(forType = HttpClient.class)
    public HttpClient httpClient(@Autowired final HttpConfig httpConfig) {
        return new DefaultHttpClient(httpConfig);
    }

    @Bean
    @Injectable(forType = FunctionRegistry.class)
    public FunctionRegistry functionRegistry() {
        return new FunctionRegistryImpl(autoRegisterSuppliers(), autoRegisterFunctions(), autoRegisterPredicates(), conversionService());
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
                JsonReplaceRule.class,
                JsonValidationRule.class);
    }

    @Bean
    public JsonRuleRegistry jsonRuleRegistry() {
        return new JsonRuleRegistryImpl(autoRegisterRules());
    }

}
