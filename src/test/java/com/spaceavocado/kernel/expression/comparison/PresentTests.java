package com.spaceavocado.kernel.expression.comparison;

import com.spaceavocado.kernel.IEvaluable;
import com.spaceavocado.kernel.operand.value.Value;
import com.spaceavocado.kernel.operand.collection.Collection;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

public class PresentTests {
    private static Stream<Arguments> evaluateData() {
        return Stream.of(
            // Truthy
            Arguments.of(new Value(1), true),
            Arguments.of(new Value(1.1), true),
            Arguments.of(new Value(1.1f), true),
            Arguments.of(new Value("1"), true),
            Arguments.of(new Value('c'), true),
            Arguments.of(new Value(true), true),
            Arguments.of(new Value(false), true),
            // Falsy
            Arguments.of(new Value(null), false)
        );
    }

    @ParameterizedTest
    @MethodSource("evaluateData")
    public void evaluate(IEvaluable operand, boolean expected) {
        var expression = new Present(operand);

        assertEquals(expected, expression.evaluate(null));
    }
}
