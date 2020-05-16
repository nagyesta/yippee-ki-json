package com.github.nagyesta.yippeekijson.core.config.parser.raw;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * Raw class for parsing JsonRule configuration.
 */
@NoArgsConstructor
@Getter
@Setter
public class RawJsonRule {
    private Integer order;
    private String name;
    private String path;
    private Map<String, Map<String, String>> params;
}
