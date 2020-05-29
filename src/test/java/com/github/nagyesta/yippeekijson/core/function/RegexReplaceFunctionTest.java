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
    private static final String ROOT_LAST = "${lastName}";
    private static final String ROOT_FIRST = "${firstName}";
    private static final String ROOT_WONT_MATCH = "${wontMatch}";
    private static final String COMMA = ", ";
    private static final String ROOT_LAST_FIRST = ROOT_LAST + COMMA + ROOT_FIRST;
    private static final String ROOT_LAST_LAST_LAST_FIRST = ROOT_LAST + COMMA + ROOT_LAST + COMMA + ROOT_LAST + COMMA + ROOT_FIRST;
    private static final String DOE_JOHN = DOE + COMMA + JOHN;
    private static final String DOE_DOE_DOE_JOHN = DOE + COMMA + DOE + COMMA + DOE + COMMA + JOHN;

    private static Object[][] validParams() {
        return new Object[][]{
                {JOHN_DOE, FIRST_LAST, ROOT_LAST_FIRST, DOE_JOHN},
                {JOHN_DOE, FIRST_LAST, ROOT_LAST_LAST_LAST_FIRST, DOE_DOE_DOE_JOHN},
                {JOHN_DOE, FIRST_LAST, ROOT_LAST, DOE},
                {JOHN_DOE, FIRST_LAST, NOTHING, NOTHING},
                {JOHN_DOE, FIRST_LAST, ROOT_WONT_MATCH, JOHN_DOE},
                {JOHN_DOE, FIRST_LAST, ROOT_FIRST, JOHN},
                {JOHN, FIRST_LAST, ROOT_WONT_MATCH, JOHN}
        };
    }

    private static Object[][] invalidParams() {
        return new Object[][]{
                {null, null},
                {null, JOHN},
                {JOHN, null}
        };
    }

    @ParameterizedTest
    @MethodSource(value = "validParams")
    void testApplyShouldReplaceMatchingParts(final String input, final String regex, final String replacement, final String expected) {
        //given
        final RegexReplaceFunction underTest = new RegexReplaceFunction(regex, replacement);

        //when
        final String actual = underTest.apply(input);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource(value = "invalidParams")
    void testConstructorShouldNotAllowNulls(final String regex, final String replacement) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new RegexReplaceFunction(regex, replacement));
    }
}
