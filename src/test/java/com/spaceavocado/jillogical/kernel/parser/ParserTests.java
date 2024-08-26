package com.spaceavocado.jillogical.kernel.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.spaceavocado.jillogical.kernel.IEvaluable;
import com.spaceavocado.jillogical.kernel.expression.comparison.Eq;
import com.spaceavocado.jillogical.kernel.expression.comparison.Ge;
import com.spaceavocado.jillogical.kernel.expression.comparison.Gt;
import com.spaceavocado.jillogical.kernel.expression.comparison.In;
import com.spaceavocado.jillogical.kernel.expression.comparison.Le;
import com.spaceavocado.jillogical.kernel.expression.comparison.Lt;
import com.spaceavocado.jillogical.kernel.expression.comparison.Ne;
import com.spaceavocado.jillogical.kernel.expression.comparison.NotIn;
import com.spaceavocado.jillogical.kernel.expression.comparison.Null;
import com.spaceavocado.jillogical.kernel.expression.comparison.Overlap;
import com.spaceavocado.jillogical.kernel.expression.comparison.Prefix;
import com.spaceavocado.jillogical.kernel.expression.comparison.Present;
import com.spaceavocado.jillogical.kernel.expression.comparison.Suffix;
import com.spaceavocado.jillogical.kernel.expression.logical.And;
import com.spaceavocado.jillogical.kernel.expression.logical.Nor;
import com.spaceavocado.jillogical.kernel.expression.logical.Not;
import com.spaceavocado.jillogical.kernel.expression.logical.Or;
import com.spaceavocado.jillogical.kernel.expression.logical.Xor;
import com.spaceavocado.jillogical.kernel.operand.collection.Collection;
import com.spaceavocado.jillogical.kernel.operand.reference.DefaultSerializeOptions;
import com.spaceavocado.jillogical.kernel.operand.reference.ISerializeOptions;
import com.spaceavocado.jillogical.kernel.operand.reference.Reference;
import com.spaceavocado.jillogical.kernel.operand.value.Value;

import java.util.stream.Stream;

public class ParserTests {
    private static ISerializeOptions serializeOptions = new DefaultSerializeOptions();
    private static String mockAddress(String value) {
        return serializeOptions.to(value);
    }

    private static Stream<Arguments> isEscapedData() {
        return Stream.of(
            Arguments.of("\\expected", true),
            Arguments.of("unexpected", false)
        );
    }

    @ParameterizedTest
    @MethodSource("isEscapedData")
    public void isEscaped(String input, boolean expected) {
        var parser = new Parser(new DefaultSerializeOptions());
        assertEquals(expected, parser.isEscaped(input));
    }

    private static Stream<Arguments> toReferenceAddressData() {
        return Stream.of(
            Arguments.of("$expected", "expected"),
            Arguments.of(null, null),
            Arguments.of(1, null)
        );
    }

    @ParameterizedTest
    @MethodSource("toReferenceAddressData")
    public void toReferenceAddress(Object reference, String expected) {
        var parser = new Parser(new DefaultSerializeOptions());
        assertEquals(expected, parser.toReferenceAddress(reference));
    }

    private static Stream<Arguments> parseValueData() {
        return Stream.of(
            Arguments.of(1, new Value(1)),
            Arguments.of(1.1, new Value(1.1)),
            Arguments.of("val", new Value("val")),
            Arguments.of(true, new Value(true))
        );
    }

    @ParameterizedTest
    @MethodSource("parseValueData")
    public void parseValue(Object input, IEvaluable expected) {
        var parser = new Parser();
        var evaluable = parser.parse(input);

        assertInstanceOf(IEvaluable.class, evaluable);
        assertEquals(expected.toString(), evaluable.toString());
    }

    private static Stream<Arguments> parseReferenceData() {
        return Stream.of(
            Arguments.of(mockAddress("address"), new Reference("address"))
        );
    }

    @ParameterizedTest
    @MethodSource("parseReferenceData")
    public void parseReference(Object input, IEvaluable expected) {
        var parser = new Parser();
        var evaluable = parser.parse(input);

        assertInstanceOf(IEvaluable.class, evaluable);
        assertEquals(expected.toString(), evaluable.toString());
    }

    private static Stream<Arguments> parseCollectionData() {
        return Stream.of(
            Arguments.of(
                new Object[] { 1 },
                new Collection(new IEvaluable[]{ new Value(1) })
            ),
            Arguments.of(
                new Object[] { mockAddress("address") },
                new Collection(new IEvaluable[]{ new Reference("address") })
            ),
            Arguments.of(
                new Object[] { "value", true },
                new Collection(new IEvaluable[]{ new Value("value"), new Value(true) })
            ),
            Arguments.of(
                new Object[] { 1, "value", true, mockAddress("address") },
                new Collection(new IEvaluable[]{ new Value(1), new Value("value"), new Value(true), new Reference("address") })
            ),
            Arguments.of(
                new Object[] { String.format("%s%s", Parser.DEFAULT_ESCAPE_CHARACTER, Parser.DEFAULT_OPERATOR_MAPPING.get(Operator.AND)), 1 },
                new Collection(new IEvaluable[]{ new Value(Parser.DEFAULT_OPERATOR_MAPPING.get(Operator.AND)), new Value(1) })
            )
        );
    }

    @ParameterizedTest
    @MethodSource("parseCollectionData")
    public void parseCollection(Object input, IEvaluable expected) {
        var parser = new Parser();
        var evaluable = parser.parse(input);

        assertInstanceOf(IEvaluable.class, evaluable);
        assertEquals(expected.toString(), evaluable.toString());
    }

    private static Stream<Arguments> parseComparisonData() {
        return Stream.of(
            Arguments.of(new Object[] { Parser.DEFAULT_OPERATOR_MAPPING.get(Operator.EQ), 1, 1 }, new Eq(new Value(1), new Value(1))),
            Arguments.of(new Object[] { Parser.DEFAULT_OPERATOR_MAPPING.get(Operator.NE), 1, 1 }, new Ne(new Value(1), new Value(1))),
            Arguments.of(new Object[] { Parser.DEFAULT_OPERATOR_MAPPING.get(Operator.GT), 1, 1 }, new Gt(new Value(1), new Value(1))),
            Arguments.of(new Object[] { Parser.DEFAULT_OPERATOR_MAPPING.get(Operator.GE), 1, 1 }, new Ge(new Value(1), new Value(1))),
            Arguments.of(new Object[] { Parser.DEFAULT_OPERATOR_MAPPING.get(Operator.LT), 1, 1 }, new Lt(new Value(1), new Value(1))),
            Arguments.of(new Object[] { Parser.DEFAULT_OPERATOR_MAPPING.get(Operator.LE), 1, 1 }, new Le(new Value(1), new Value(1))),
            Arguments.of(new Object[] { Parser.DEFAULT_OPERATOR_MAPPING.get(Operator.IN), 1, 1 }, new In(new Value(1), new Value(1))),
            Arguments.of(new Object[] { Parser.DEFAULT_OPERATOR_MAPPING.get(Operator.NOTIN), 1, 1 }, new NotIn(new Value(1), new Value(1))),
            Arguments.of(new Object[] { Parser.DEFAULT_OPERATOR_MAPPING.get(Operator.NONE), 1 }, new Null(new Value(1))),
            Arguments.of(new Object[] { Parser.DEFAULT_OPERATOR_MAPPING.get(Operator.PRESENT), 1 }, new Present(new Value(1))),
            Arguments.of(new Object[] { Parser.DEFAULT_OPERATOR_MAPPING.get(Operator.PREFIX), 1, 1 }, new Prefix(new Value(1), new Value(1))),
            Arguments.of(new Object[] { Parser.DEFAULT_OPERATOR_MAPPING.get(Operator.SUFFIX), 1, 1 }, new Suffix(new Value(1), new Value(1))),
            Arguments.of(new Object[] { Parser.DEFAULT_OPERATOR_MAPPING.get(Operator.OVERLAP), 1, 1 }, new Overlap(new Value(1), new Value(1)))
        );
    }

    @ParameterizedTest
    @MethodSource("parseComparisonData")
    public void parseComparison(Object input, IEvaluable expected) {
        var parser = new Parser();
        var evaluable = parser.parse(input);

        assertInstanceOf(IEvaluable.class, evaluable);
        assertEquals(expected.toString(), evaluable.toString());
    }

    private static Stream<Arguments> parseLogicalData() {
        return Stream.of(
            Arguments.of(new Object[] { Parser.DEFAULT_OPERATOR_MAPPING.get(Operator.AND), true, true }, new And(new IEvaluable[]{ new Value(true), new Value(true) })),
            Arguments.of(new Object[] { Parser.DEFAULT_OPERATOR_MAPPING.get(Operator.OR), true, true, false }, new Or(new IEvaluable[]{ new Value(true), new Value(true), new Value(false) })),
            Arguments.of(new Object[] { Parser.DEFAULT_OPERATOR_MAPPING.get(Operator.NOR), true, true }, new Nor(new IEvaluable[]{ new Value(true), new Value(true) })),
            Arguments.of(new Object[] { Parser.DEFAULT_OPERATOR_MAPPING.get(Operator.XOR), true, true }, new Xor(new IEvaluable[]{ new Value(true), new Value(true) })),
            Arguments.of(new Object[] { Parser.DEFAULT_OPERATOR_MAPPING.get(Operator.NOT), true }, new Not(new Value(true)))
        );
    }

    @ParameterizedTest
    @MethodSource("parseLogicalData")
    public void parseLogical(Object input, IEvaluable expected) {
        var parser = new Parser();
        var evaluable = parser.parse(input);

        assertInstanceOf(IEvaluable.class, evaluable);
        assertEquals(expected.toString(), evaluable.toString());
    }

    @Test
    public void unexpectedExpressionInputException()
    {
        var parser = new Parser();
        assertThrows(UnexpectedExpressionInputException.class, () -> parser.parse(null));
    } 

    @Test
    public void unexpectedOperandException()
    {
        var parser = new Parser();
        assertThrows(UnexpectedOperandException.class, () -> parser.parse(new Object[] { }));
    } 

    private static Stream<Arguments> unexpectedExpressionExceptionData() {
        return Stream.of(
            Arguments.of(new Object[] { "X", 1, 1 }, null),
            Arguments.of(new Object[] { 1, 1, 1 }, null)
        );
    }

    @ParameterizedTest
    @MethodSource("unexpectedExpressionExceptionData")
    public void unexpectedExpressionException(Object[] input) {
        var parser = new Parser();
        assertThrows(UnexpectedExpressionException.class, () -> parser.createExpression(input));
    }
}
