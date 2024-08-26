package com.spaceavocado.jillogical.kernel.operand.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.NullSource;

import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class ValueTests {
    private static Stream<Arguments> evaluateData() {
        return Stream.of(
            Arguments.of(1),
            Arguments.of(1.1d),
            Arguments.of(1.1f),
            Arguments.of("val"),
            Arguments.of('c'),
            Arguments.of(true),
            Arguments.of(false)
        );
    }

    @ParameterizedTest
    @MethodSource("evaluateData")
    @NullSource
    public void evaluate(Object input) {
        var value = new Value(input);
        assertEquals(input, value.evaluate(null));
    }

    private static Stream<Arguments> serializeData() {
        return Stream.of(
            Arguments.of(1),
            Arguments.of(1.1d),
            Arguments.of(1.1f),
            Arguments.of("val"),
            Arguments.of('c'),
            Arguments.of(true),
            Arguments.of(false)
        );
    }

    @ParameterizedTest
    @MethodSource("serializeData")
    @NullSource
    public void serialize(Object input) {
        var value = new Value(input);
        assertEquals(input, value.serialize());
    }

    private static Stream<Arguments> simplifyData() {
        return Stream.of(
            Arguments.of(1),
            Arguments.of(1.1d),
            Arguments.of(1.1f),
            Arguments.of("val"),
            Arguments.of('c'),
            Arguments.of(true),
            Arguments.of(false)
        );
    }

    @ParameterizedTest
    @MethodSource("simplifyData")
    @NullSource
    public void simplify(Object input) {
        var value = new Value(input);
        assertEquals(input, value.simplify(null));
    }

    private static Stream<Arguments> toStringData() {
        return Stream.of(
            Arguments.of(1, "1"),
            Arguments.of(1.1d, "1.1"),
            Arguments.of(1.1f, "1.1"),
            Arguments.of("val", "\"val\""),
            Arguments.of('c', "\"c\""),
            Arguments.of(true, "true"),
            Arguments.of(false, "false")
        );
    }

    @ParameterizedTest
    @MethodSource("toStringData")
    public void toString(Object input, String expected) {
        var value = new Value(input);
        assertEquals(expected, value.toString());
    }
}
