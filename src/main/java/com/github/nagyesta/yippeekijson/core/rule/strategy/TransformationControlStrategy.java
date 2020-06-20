package com.github.nagyesta.yippeekijson.core.rule.strategy;

import com.github.nagyesta.yippeekijson.core.exception.AbortTransformationException;
import com.github.nagyesta.yippeekijson.core.exception.StopRuleProcessingException;
import com.networknt.schema.ValidationMessage;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * Defines certain strategies for the validation error handling on the rule chain level.
 */
@Slf4j
public enum TransformationControlStrategy {
    /**
     * Aborts the processing of the file entirely withut producing an output file.
     */
    ABORT() {
        public void accept(@NotNull final Set<ValidationMessage> violations) {
            if (!CollectionUtils.isEmpty(violations)) {
                throw new AbortTransformationException("Abort: JSON document is invalid. " + violations.size() + " violations found.");
            }
        }
    },
    /**
     * Skips the rest of the rule processing of the file, handling the partial data we have as final result.
     * Useful for saving validation errors right into the JSON.
     */
    SKIP_REST() {
        public void accept(@NotNull final Set<ValidationMessage> violations) {
            if (!CollectionUtils.isEmpty(violations)) {
                throw new StopRuleProcessingException("Stop: JSON document is invalid. " + violations.size() + " violations found.");
            }
        }
    },
    /**
     * Does nothing, the rule processing can continue regardless of the validation failures.
     */
    CONTINUE() {
        @Override
        public void accept(@NotNull final Set<ValidationMessage> violations) {
            if (!CollectionUtils.isEmpty(violations)) {
                log.info("Ignore: JSON document is invalid. " + violations.size() + " violations found.");
            }
        }
    };

    /**
     * Evaluates the validation result and triggers a response if needed.
     *
     * @param violations The validation results.
     */
    public abstract void accept(@NotNull Set<ValidationMessage> violations);
}
