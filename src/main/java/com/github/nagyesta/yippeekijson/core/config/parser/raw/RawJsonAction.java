package com.github.nagyesta.yippeekijson.core.config.parser.raw;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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

        final RawJsonAction that = (RawJsonAction) o;

        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
