package com.github.nagyesta.yippeekijson.core.config.parser.raw;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

/**
 * Raw class for parsing the root of the configuration.
 */
@NoArgsConstructor
@Getter
@Setter
public class RawJsonActions {
    @NonNull
    @Valid
    private List<RawJsonAction> actions = Collections.emptyList();
}
