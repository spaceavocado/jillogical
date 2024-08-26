package com.spaceavocado.jillogical.kernel.expression.comparison;

import com.spaceavocado.jillogical.kernel.IEvaluable;
import com.spaceavocado.jillogical.kernel.operand.value.Value;
import com.spaceavocado.jillogical.kernel.operand.collection.Collection;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

public class NullTests {
    private static Stream<Arguments> evaluateData() {
        return Stream.of(
            // Truthy
            Arguments.of(new Value(null), true),
            // Falsy
            Arguments.of(new Value(1), false),
            Arguments.of(new Value(1.1), false),
            Arguments.of(new Value(1.1f), false),
            Arguments.of(new Value("1"), false),
            Arguments.of(new Value('c'), false),
            Arguments.of(new Value(true), false),
            Arguments.of(new Value(false), false)
        );
    }

    @ParameterizedTest
    @MethodSource("evaluateData")
    public void evaluate(IEvaluable operand, boolean expected) {
        var expression = new Null(operand);

        assertEquals(expected, expression.evaluate(null));
    }
}
