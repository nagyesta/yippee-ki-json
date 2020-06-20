package com.github.nagyesta.yippeekijson.core.config.parser.raw;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.List;

/**
 * Raw class for parsing JsonAction configuration.
 */
@NoArgsConstructor
@Getter
@Setter
public class RawJsonAction {
    @NonNull
    @NotBlank
    private String name;
    @NonNull
    @Valid
    private List<RawJsonRule> rules = Collections.emptyList();


    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof RawJsonAction)) {
            return false;
        }

        RawJsonAction that = (RawJsonAction) o;

        return new EqualsBuilder()
                .append(name, that.name)
                .isEquals();
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .toHashCode();
    }
}
