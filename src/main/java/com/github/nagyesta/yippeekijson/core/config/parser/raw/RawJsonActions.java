package com.github.nagyesta.yippeekijson.core.config.parser.raw;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Raw class for parsing the root of the configuration.
 */
@NoArgsConstructor
@Getter
@Setter
public class RawJsonActions {

   private List<RawJsonAction> actions;
}
