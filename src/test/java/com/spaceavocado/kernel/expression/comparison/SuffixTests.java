package com.spaceavocado.kernel.expression.comparison;

import com.spaceavocado.kernel.IEvaluable;
import com.spaceavocado.kernel.operand.value.Value;
import com.spaceavocado.kernel.operand.collection.Collection;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

public class SuffixTests {
    private static Stream<Arguments> evaluateData() {
        return Stream.of(
             // Truthy
            Arguments.of(new Value("bogus"), new Value("us"), true),
            Arguments.of(new Value("bogus"), new Value('s'), true),
            Arguments.of(new Value("b"), new Value('b'), true),
            // Falsy
            Arguments.of(new Value("bogus"), new Value("bogu"), false),
            Arguments.of(new Value(1), new Value(1.1), false),
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
        var expression = new Suffix(left, right);

        assertEquals(expected, expression.evaluate(null));
    }
}
