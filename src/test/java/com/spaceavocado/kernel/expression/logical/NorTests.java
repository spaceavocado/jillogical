package com.spaceavocado.kernel.expression.logical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import org.junit.jupiter.params.provider.Arguments;

import com.spaceavocado.kernel.EvaluateException;
import com.spaceavocado.kernel.FlattenContext;
import com.spaceavocado.kernel.IEvaluable;
import com.spaceavocado.kernel.operand.reference.Reference;
import com.spaceavocado.kernel.operand.value.Value;

import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;


public class NorTests {
    @Test
    public void invalidArgument() {
        assertThrows(IllegalArgumentException.class, () -> new Nor(new IEvaluable[] { new Value(true) }));
    }

    private static Stream<Arguments> evaluateData() {
        return Stream.of(
            // Truthy
            Arguments.of(new IEvaluable[] { new Value(false), new Value(false) }, true),
            Arguments.of(new IEvaluable[] { new Value(false), new Value(false), new Value(false) }, true),
            // Falsy
            Arguments.of(new IEvaluable[] { new Value(true), new Value(true) }, false),
            Arguments.of(new IEvaluable[] { new Value(false), new Value(true) }, false),
            Arguments.of(new IEvaluable[] { new Value(true), new Value(true) }, false),
            Arguments.of(new IEvaluable[] { new Value(true), new Value(true), new Value(false) }, false)
        );
    }

    @ParameterizedTest
    @MethodSource("evaluateData")
    public void evaluate(IEvaluable[] operands, boolean expected) {
        var expression = new Nor(operands);
        assertEquals(expected, expression.evaluate(null));
    }

    private static Stream<Arguments> evaluateInvalidOperandData() {
        return Stream.of(
            Arguments.of(new IEvaluable[] { new Value(1), new Value(true) }, null),
            Arguments.of(new IEvaluable[] { new Value(false), new Value(1) }, null),
            Arguments.of(new IEvaluable[] { new Value(1), new Value("bogus") }, null)
        );
    }

    @ParameterizedTest
    @MethodSource("evaluateInvalidOperandData")
    public void evaluateInvalidOperand(IEvaluable[] operands) {
        var expression = new Nor(operands);
        assertThrows(EvaluateException.class, () -> expression.evaluate(null));
    }

    private static Stream<Arguments> simplifyData() {
        return Stream.of(
            Arguments.of(new IEvaluable[] { new Value(false), new Value(false) }, true),
            Arguments.of(new IEvaluable[] { new Value(false), new Value(true) }, false),
            Arguments.of(new IEvaluable[] { new Value(true), new Value(false) }, false),
            Arguments.of(new IEvaluable[] { new Value(true), new Value(true) }, false),
            Arguments.of(new IEvaluable[] { new Reference("RefA"), new Value(false) }, false),
            Arguments.of(new IEvaluable[] { new Reference("Missing"), new Value(false) }, new Not(new Reference("Missing"))),
            Arguments.of(new IEvaluable[] {
                new Reference("Missing"), new Reference("Missing") },
                new Nor(new IEvaluable[] { new Reference("Missing"), new Reference("Missing") })
            ),
            Arguments.of(new IEvaluable[] { new Value(false), new Reference("invalid") }, new Not(new Reference("invalid")))
        );
    }

    @ParameterizedTest
    @MethodSource("simplifyData")
    public void simplify(IEvaluable[] operands, Object expected) {
        var expression = new Nor(operands);
        var simplified = expression.simplify(FlattenContext.toHashMap(Map.of("RefA", true, "invalid", 1)));

        if (simplified instanceof IEvaluable) {
            assertEquals(expected.toString(), simplified.toString());
        } else {
            assertEquals(simplified, expected);
        } 
    }
}
