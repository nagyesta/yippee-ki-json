package com.github.nagyesta.yippeekijson;

import com.github.nagyesta.yippeekijson.core.config.entities.JsonActions;
import com.github.nagyesta.yippeekijson.core.config.parser.ActionConfigParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "logging.level.root=DEBUG")
class YippeeKiJsonApplicationTests {

    @Autowired
    private ActionConfigParser actionConfigParser;

    @Test
    void contextLoads() {
        final JsonActions parse = actionConfigParser.parse(this.getClass().getResourceAsStream("/rulesets/example.yml"));
        Assertions.assertNotNull(parse);
        Assertions.assertEquals(2, parse.getActions().size());
        Assertions.assertEquals(3, parse.getActions().get("filter").getRules().size());
        Assertions.assertEquals(6, parse.getActions().get("split-name").getRules().size());
    }

}
