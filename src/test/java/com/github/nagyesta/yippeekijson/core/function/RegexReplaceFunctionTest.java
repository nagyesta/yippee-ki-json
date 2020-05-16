package com.github.nagyesta.yippeekijson.core.function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class RegexReplaceFunctionTest {

    private static final String FIRST_LAST = "^(?<firstName>[A-Za-z\\-]+) (?<lastName>[A-Za-z\\-]+)$";
    private static final String JOHN = "John";
    private static final String NOTHING = "Nothing";
    private static final String DOE = "Doe";
    private static final String JOHN_DOE = JOHN + " " + DOE;
    private static final String $_LAST = "${lastName}";
    private static final String $_FIRST = "${firstName}";
    private static final String $_WONT_MATCH = "${wontMatch}";
    private static final String COMMA = ", ";
    private static final String $_LAST_FIRST = $_LAST + COMMA + $_FIRST;
    private static final String $_LAST_LAST_LAST_FIRST = $_LAST + COMMA + $_LAST + COMMA + $_LAST + COMMA + $_FIRST;
    private static final String DOE_JOHN = DOE + COMMA + JOHN;
    private static final String DOE_DOE_DOE_JOHN = DOE + COMMA + DOE + COMMA + DOE + COMMA + JOHN;

    private static Object[][] validParams() {
        return new Object[][]{
                {JOHN_DOE, FIRST_LAST, $_LAST_FIRST, DOE_JOHN},
                {JOHN_DOE, FIRST_LAST, $_LAST_LAST_LAST_FIRST, DOE_DOE_DOE_JOHN},
                {JOHN_DOE, FIRST_LAST, $_LAST, DOE},
                {JOHN_DOE, FIRST_LAST, NOTHING, NOTHING},
                {JOHN_DOE, FIRST_LAST, $_WONT_MATCH, JOHN_DOE},
                {JOHN_DOE, FIRST_LAST, $_FIRST, JOHN},
                {JOHN, FIRST_LAST, $_WONT_MATCH, JOHN}
        };
    }

    private static Object[][] invalidParams() {
        return new Object[][] {
                {null, null},
                {null, JOHN},
                {JOHN, null}
        };
    }

    @ParameterizedTest
    @MethodSource(value = "validParams")
    void testApplyShouldReplaceMatchingParts(String input, String regex, String replacement, String expected) {
        //given
        RegexReplaceFunction underTest = new RegexReplaceFunction(regex, replacement);

        //when
        String actual = underTest.apply(input);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource(value = "invalidParams")
    void testConstructorShouldNotAllowNulls(String regex, String replacement) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new RegexReplaceFunction(regex, replacement));
    }
}