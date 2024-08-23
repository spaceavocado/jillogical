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


public class NotTests {
    private static Stream<Arguments> evaluateData() {
        return Stream.of(
             // Truthy
            Arguments.of(new Value(false), true),
            // Falsy
            Arguments.of(new Value(true), false)
        );
    }

    @ParameterizedTest
    @MethodSource("evaluateData")
    public void evaluate(IEvaluable operand, boolean expected) {
        var expression = new Not(operand);
        assertEquals(expected, expression.evaluate(null));
    }

    private static Stream<Arguments> evaluateInvalidOperandData() {
        return Stream.of(
            Arguments.of(new Value(1)),
            Arguments.of(new Value("bogus"))
        );
    }

    @ParameterizedTest
    @MethodSource("evaluateInvalidOperandData")
    public void evaluateInvalidOperand(IEvaluable operand) {
        var expression = new Not(operand);
        assertThrows(EvaluateException.class, () -> expression.evaluate(null));
    }

    private static Stream<Arguments> simplifyData() {
        return Stream.of(
            Arguments.of(new Value(false), true),
            Arguments.of(new Value(true), false),
            Arguments.of(new Reference("RefA"), false),
            Arguments.of(new Reference("Missing"), new Not(new Reference("Missing"))),
            Arguments.of(new Reference("invalid"), new Not(new Reference("invalid")))
        );
    }

    @ParameterizedTest
    @MethodSource("simplifyData")
    public void simplify(IEvaluable operand, Object expected) {
        var expression = new Not(operand);
        var simplified = expression.simplify(FlattenContext.toHashMap(Map.of("RefA", true, "invalid", 1)));

        if (simplified instanceof IEvaluable) {
            assertEquals(expected.toString(), simplified.toString());
        } else {
            assertEquals(simplified, expected);
        } 
    }
}
