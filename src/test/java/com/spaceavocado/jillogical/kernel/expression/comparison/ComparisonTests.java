package com.spaceavocado.jillogical.kernel.expression.comparison;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.params.ParameterizedTest;

import org.junit.jupiter.params.provider.Arguments;

import com.spaceavocado.jillogical.kernel.FlattenContext;
import com.spaceavocado.jillogical.kernel.IEvaluable;
import com.spaceavocado.jillogical.kernel.operand.value.Value;
import com.spaceavocado.jillogical.kernel.operand.reference.Reference;

import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;


public class ComparisonTests {
    private static Comparison comparisonMock(String operator, IEvaluable... operands) {
        return new ComparisonSample(
            operator,
            operator,
            new IComparison() {
                @Override
                public boolean evaluate(Object... operands) {
                    return operands[0].equals(operands[1]);
                }
            },
            operands
        );
    }

    private static Stream<Arguments> evaluateData() {
        return Stream.of(
            Arguments.of(new Value(1), new Value(1), true),
            Arguments.of(new Value(1), new Value("1"), false)
        );
    }

    @ParameterizedTest
    @MethodSource("evaluateData")
    public void evaluate(IEvaluable left, IEvaluable right, boolean expected) {
        var expression = comparisonMock("==", left, right);

        assertEquals(expected, expression.evaluate(null));
    }

    private static Stream<Arguments> evaluateExceptionData() {
        return Stream.of(
            Arguments.of(new RogueOperand(), new RogueOperand())
        );
    }

    @ParameterizedTest
    @MethodSource("evaluateExceptionData")
    public void evaluateException(IEvaluable left, IEvaluable right) {
        var expression = comparisonMock("==", left, right);

        assertEquals(false, expression.evaluate(null));
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
        var expression = comparisonMock(operator, operators);

        assertArrayEquals(expected, (Object[])expression.serialize());
    }

    private static Stream<Arguments> simplifyData() {
        return Stream.of(
            Arguments.of(
                new Value(0),
                new Reference("Missing"),
                comparisonMock("==", new Value(0), new Reference("Missing"))
            ),
            Arguments.of(
                new Reference("Missing"),
                new Value(0),
                comparisonMock("==", new Reference("Missing"), new Value(0))
            ),
            Arguments.of(
                new Reference("Missing"),
                new Reference("Missing"),
                comparisonMock("==", new Reference("Missing"), new Reference("Missing"))
            ),
            Arguments.of(new Value(0), new Value(0), true),
            Arguments.of(new Value(0), new Value(1), false),
            Arguments.of(new Value("A"), new Reference("RefA"), true)
        );
    }

    @ParameterizedTest
    @MethodSource("simplifyData")
    public void simplify(IEvaluable left, IEvaluable right, Object expected) {
        var expression = comparisonMock("==", left, right);
        var simplified = expression.simplify(FlattenContext.toHashMap(Map.of("RefA", "A")));

        if (simplified instanceof IEvaluable evaluable) {
            assertEquals(expected.toString(), evaluable.toString());
        } else {
            assertEquals(expected, simplified);
        }
    }

    private static Stream<Arguments> toStringData() {
        return Stream.of(
            Arguments.of("==", new IEvaluable[] { new Value(1), new Value(2) }, "(1 == 2)"),
            Arguments.of("<null>", new IEvaluable[] { new Value(1) }, "(1 <null>)")
        );
    }

    @ParameterizedTest
    @MethodSource("toStringData")
    public void toString(String operator, IEvaluable[] operators, String expected) {
        var expression = comparisonMock(operator, operators);
        assertEquals(expected, expression.toString());
    }

    private static Stream<Arguments> isNumberData() {
        return Stream.of(
            Arguments.of(1, true),
            Arguments.of(1f, true),
            Arguments.of(1d, true),
            Arguments.of("1", false),
            Arguments.of('c', false),
            Arguments.of(true, false),
            Arguments.of(false, false),
            Arguments.of(null, false),
            Arguments.of(new Object[] {}, false)
        );
    }

    @ParameterizedTest
    @MethodSource("isNumberData")
    public void isNumber(Object value, boolean expected) {
        assertEquals(expected, Comparison.isNumber(value));
    }

    private static Stream<Arguments> isTextData() {
        return Stream.of(
            Arguments.of("1", true),
            Arguments.of('c', true),
            Arguments.of(1, false),
            Arguments.of(1f, false),
            Arguments.of(1d, false),
            Arguments.of(true, false),
            Arguments.of(false, false),
            Arguments.of(null, false),
            Arguments.of(new Object[] { }, false)
        );
    }

    @ParameterizedTest
    @MethodSource("isTextData")
    public void isText(Object value, boolean expected) {
        assertEquals(expected, Comparison.isText(value));
    }
}
