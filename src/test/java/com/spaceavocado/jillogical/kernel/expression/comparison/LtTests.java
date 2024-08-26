package com.spaceavocado.jillogical.kernel.expression.comparison;

import com.spaceavocado.jillogical.kernel.IEvaluable;
import com.spaceavocado.jillogical.kernel.operand.value.Value;
import com.spaceavocado.jillogical.kernel.operand.collection.Collection;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

public class LtTests {
    private static Stream<Arguments> evaluateData() {
        return Stream.of(
             // Truthy
            Arguments.of(new Value(1), new Value(2), true),
            Arguments.of(new Value(1.2), new Value(1.3), true),
            Arguments.of(new Value(1.2f), new Value(1.3f), true),
            // Falsy
            Arguments.of(new Value(1), new Value(1), false),
            Arguments.of(new Value(1.1), new Value(1.1), false),
            Arguments.of(new Value(1.1f), new Value(1.1f), false),
            Arguments.of(new Value(1), new Value(0), false),
            Arguments.of(new Value(1.0f), new Value(0.9f), false),
            Arguments.of(new Value(1), new Value("1"), false),
            Arguments.of(new Value(1), new Value(true), false),
            Arguments.of(new Value(1.1), new Value("1"), false),
            Arguments.of(new Value(1.1), new Value(true), false),
            Arguments.of(new Value("1"), new Value(true), false),
            Arguments.of(new Value(null), new Value(1), false),
            Arguments.of(new Value(1), new Value(null), false),
            Arguments.of(new Collection(new IEvaluable[]{ new Value(1) }), new Collection(new IEvaluable[]{ new Value(1) }), false),
            Arguments.of(new Value(1), new Collection(new IEvaluable[]{ new Value(1) }), false)
        );
    }

    @ParameterizedTest
    @MethodSource("evaluateData")
    public void evaluate(IEvaluable left, IEvaluable right, boolean expected) {
        var expression = new Lt(left, right);

        assertEquals(expected, expression.evaluate(null));
    }
}
