package com.github.nagyesta.yippeekijson.core.config.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class JsonPathValidator implements ConstraintValidator<JsonPath, String> {

    private JsonPath constraintAnnotation;

    @Override
    @SuppressWarnings("checkstyle:HiddenField")
    public void initialize(final JsonPath constraintAnnotation) {
        this.constraintAnnotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(@Nullable final String value, @NotNull final ConstraintValidatorContext context) {
        boolean result = false;
        if (StringUtils.hasText(value)) {
            try {
                com.jayway.jsonpath.JsonPath.compile(value);
                result = true;
            } catch (final Exception ex) {
                log.warn(ex.getMessage());
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(constraintAnnotation.message()).addConstraintViolation();
            }
        }
        return result;
    }
}
