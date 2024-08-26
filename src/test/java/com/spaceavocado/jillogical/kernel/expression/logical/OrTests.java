package com.spaceavocado.jillogical.kernel.expression.logical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import org.junit.jupiter.params.provider.Arguments;

import com.spaceavocado.jillogical.kernel.EvaluateException;
import com.spaceavocado.jillogical.kernel.FlattenContext;
import com.spaceavocado.jillogical.kernel.IEvaluable;
import com.spaceavocado.jillogical.kernel.operand.reference.Reference;
import com.spaceavocado.jillogical.kernel.operand.value.Value;

import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;


public class OrTests {
    @Test
    public void invalidArgument() {
        assertThrows(IllegalArgumentException.class, () -> new Or(new IEvaluable[] { new Value(true) }));
    }

    private static Stream<Arguments> evaluateData() {
        return Stream.of(
            // Truthy
            Arguments.of(new IEvaluable[] { new Value(true), new Value(true) }, true),
            Arguments.of(new IEvaluable[] { new Value(false), new Value(true) }, true),
            Arguments.of(new IEvaluable[] { new Value(true), new Value(true), new Value(false) }, true),
            Arguments.of(new IEvaluable[] { new Value(true), new Value(1) }, true),
            // Falsy
            Arguments.of(new IEvaluable[] { new Value(false), new Value(false) }, false)
        );
    }

    @ParameterizedTest
    @MethodSource("evaluateData")
    public void evaluate(IEvaluable[] operands, boolean expected) {
        var expression = new Or(operands);
        assertEquals(expected, expression.evaluate(null));
    }

    private static Stream<Arguments> evaluateInvalidOperandData() {
        return Stream.of(
            Arguments.of(new IEvaluable[] { new Value(1), new Value(true) }, null),
            Arguments.of(new IEvaluable[] { new Value(1), new Value("bogus") }, null)
        );
    }

    @ParameterizedTest
    @MethodSource("evaluateInvalidOperandData")
    public void evaluateInvalidOperand(IEvaluable[] operands) {
        var expression = new Or(operands);
        assertThrows(EvaluateException.class, () -> expression.evaluate(null));
    }

    private static Stream<Arguments> simplifyData() {
        return Stream.of(
            Arguments.of(new IEvaluable[] { new Value(true), new Value(true) }, true),
            Arguments.of(new IEvaluable[] { new Value(false), new Value(true) }, true),
            Arguments.of(new IEvaluable[] { new Value(true), new Value(1) }, true),
            Arguments.of(new IEvaluable[] { new Reference("RefA"), new Value(false) }, true),
            Arguments.of(new IEvaluable[] { new Reference("Missing"), new Value(false) }, new Reference("Missing")),
            Arguments.of(new IEvaluable[] {
                new Reference("Missing"), new Reference("Missing") },
                new Or(new IEvaluable[] { new Reference("Missing"), new Reference("Missing") })
            ),
            Arguments.of(new IEvaluable[] { new Value(false), new Reference("invalid"), new Value(false) }, new Reference("invalid"))
        );
    }

    @ParameterizedTest
    @MethodSource("simplifyData")
    public void simplify(IEvaluable[] operands, Object expected) {
        var expression = new Or(operands);
        var simplified = expression.simplify(FlattenContext.toHashMap(Map.of("RefA", true, "invalid", 1)));

        if (simplified instanceof IEvaluable) {
            assertEquals(expected.toString(), simplified.toString());
        } else {
            assertEquals(simplified, expected);
        } 
    }
}
