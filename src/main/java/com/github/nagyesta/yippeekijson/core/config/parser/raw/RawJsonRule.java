package com.github.nagyesta.yippeekijson.core.config.parser.raw;

import com.github.nagyesta.yippeekijson.core.config.validation.JsonPath;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.Map;

/**
 * Raw class for parsing JsonRule configuration.
 */
@NoArgsConstructor
@Getter
@Setter
public class RawJsonRule {
    @NonNull
    private Integer order;
    @NonNull
    @NotBlank
    private String name;
    @NonNull
    @JsonPath
    private String path;
    @NonNull
    @Valid
    private Map<String, Map<String, String>> params = Collections.emptyMap();
}
