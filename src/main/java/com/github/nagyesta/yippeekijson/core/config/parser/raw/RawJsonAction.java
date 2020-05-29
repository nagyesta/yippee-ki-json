package com.github.nagyesta.yippeekijson.core.config.parser.raw;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

/**
 * Raw class for parsing JsonAction configuration.
 */
@NoArgsConstructor
@Getter
@Setter
public class RawJsonAction {

    private String name;

    private List<RawJsonRule> rules;

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
        if (name != null) {
            return name.hashCode();
        }
        return 0;
    }
}
