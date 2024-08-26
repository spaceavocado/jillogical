package com.spaceavocado.jillogical.kernel.expression.logical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.params.ParameterizedTest;

import org.junit.jupiter.params.provider.Arguments;

import com.spaceavocado.jillogical.kernel.IEvaluable;
import com.spaceavocado.jillogical.kernel.operand.value.Value;

import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;


public class LogicalTests {
    private static Logical logicalMock(String operator, IEvaluable... operands) {
        return new LogicalSample(operator, operator, operands);
    }

    private static Stream<Arguments> serializeData() {
        return Stream.of(
            Arguments.of("->", new IEvaluable[] { new Value(1), new Value(2) }, new Object[] { "->", 1, 2 }),
            Arguments.of("X", new IEvaluable[] { new Value(1) }, new Object[] { "X", 1 })
        );
    }

    @ParameterizedTest
    @MethodSource("serializeData")
    public void serialize(String operator, IEvaluable[] operators, Object[] expected) {
        var expression = logicalMock(operator, operators);

        assertArrayEquals(expected, (Object[])expression.serialize());
    }

    private static Stream<Arguments> toStringData() {
        return Stream.of(
            Arguments.of("->", new IEvaluable[] { new Value(1), new Value("2") }, "(1 -> \"2\")"),
            Arguments.of("->", new IEvaluable[] { new Value(1), new Value("2"), new Value(1) }, "(1 -> \"2\" -> 1)"),
            Arguments.of("X", new IEvaluable[] { new Value(1) }, "(X 1)")
        );
    }

    @ParameterizedTest
    @MethodSource("toStringData")
    public void toString(String operator, IEvaluable[] operators, String expected) {
        var expression = logicalMock(operator, operators);
        assertEquals(expected, expression.toString());
    }
}
