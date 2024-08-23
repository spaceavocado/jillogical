package com.spaceavocado.kernel.expression.comparison;

import com.spaceavocado.kernel.IEvaluable;
import com.spaceavocado.kernel.operand.value.Value;
import com.spaceavocado.kernel.operand.collection.Collection;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

public class NeTests {
    private static Stream<Arguments> evaluateData() {
        return Stream.of(
            // Truthy
            Arguments.of(new Value(1), new Value(1.1), true),
            Arguments.of(new Value(1), new Value("1"), true),
            Arguments.of(new Value(1), new Value(true), true),
            Arguments.of(new Value(1.1), new Value("1"), true),
            Arguments.of(new Value(1.1), new Value(true), true),
            Arguments.of(new Value("1"), new Value(true), true),
            Arguments.of(new Value(null), new Value(1), true),
            Arguments.of(new Value(1), new Value(null), true),
            Arguments.of(new Collection(new IEvaluable[]{ new Value(1) }), new Collection(new IEvaluable[]{ new Value(1) }), true),
            Arguments.of(new Value(1), new Collection(new IEvaluable[]{ new Value(1) }), true),
            // Falsy
            Arguments.of(new Value(1), new Value(1), false),
            Arguments.of(new Value(1.1), new Value(1.1), false),
            Arguments.of(new Value(1.1f), new Value(1.1f), false),
            Arguments.of(new Value("1"), new Value("1"), false),
            Arguments.of(new Value(true), new Value(true), false),
            Arguments.of(new Value(false), new Value(false), false),
            Arguments.of(new Value(null), new Value(null), false)
        );
    }

    @ParameterizedTest
    @MethodSource("evaluateData")
    public void evaluate(IEvaluable left, IEvaluable right, boolean expected) {
        var expression = new Ne(left, right);

        assertEquals(expected, expression.evaluate(null));
    }
}
