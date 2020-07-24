package com.github.nagyesta.yippeekijson.core.config.validation;

import com.github.nagyesta.yippeekijson.core.config.validation.YippeeConfigValidator.FailureReasonCode;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class FileConstraintValidator implements ConstraintValidator<ValidFile, String> {

    private FileValidator validator;
    private Map<FailureReasonCode, String> messages;
    private Boolean mustExist;

    @Override
    public void initialize(final ValidFile constraint) {
        mustExist = constraint.exists().getCheck();
        validator = new FileValidator(null,
                constraint.exists().getCheck(),
                constraint.canRead().getCheck(),
                constraint.canWrite().getCheck(),
                constraint.isDirectory().getCheck());
        final Map<FailureReasonCode, String> map = Arrays.stream(constraint.messages())
                .collect(Collectors.toMap(MessageCode::reason, MessageCode::message));
        this.messages = Collections.unmodifiableMap(map);
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        final File file = new File(value);
        if (mustExist == Boolean.TRUE || file.exists()) {
            return validator.isValid(file, context, this.messages);
        }
        return true;
    }
}
