package com.spaceavocado.kernel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class PrimitiveTests {
    private static Stream<Arguments> isPrimitiveData() {
        return Stream.of(
            Arguments.of(1, true),
            Arguments.of(1.1d, true),
            Arguments.of(1.1f, true),
            Arguments.of("val", true),
            Arguments.of('c', true),
            Arguments.of(true, true),
            Arguments.of(false, true),
            Arguments.of(null, false),
            Arguments.of(new int[] { 1 }, false)
        );
    }

    @ParameterizedTest
    @MethodSource("isPrimitiveData")
    public void isPrimitive(Object input, boolean expected) {
        assertEquals(expected, Primitive.isPrimitive(input));
    }
}
