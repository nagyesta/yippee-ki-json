package com.github.nagyesta.yippeekijson;

import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.healthcheck.impl.PercentageBasedMissionHealthCheckEvaluator;
import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;
import com.github.nagyesta.abortmission.core.outline.MissionOutline;

import java.util.Map;
import java.util.function.Consumer;

import static com.github.nagyesta.abortmission.core.MissionControl.matcher;
import static com.github.nagyesta.abortmission.core.MissionControl.percentageBasedEvaluator;

public class MissionOutlineDefinition extends MissionOutline {

    @Override
    protected Map<String, Consumer<AbortMissionCommandOps>> defineOutline() {
        MissionHealthCheckMatcher integrationTestMatcher = matcher()
                .classNamePattern(".+IntegrationTest")
                .build();
        PercentageBasedMissionHealthCheckEvaluator evaluator = percentageBasedEvaluator(integrationTestMatcher)
                .abortThreshold(0).build();
        return Map.of(MissionOutline.SHARED_CONTEXT, ops -> ops.registerHealthCheck(evaluator));
    }
}
