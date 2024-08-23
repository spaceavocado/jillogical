package com.spaceavocado;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.spaceavocado.kernel.IEvaluable;
import com.spaceavocado.kernel.expression.comparison.Eq;
import com.spaceavocado.kernel.expression.logical.And;
import com.spaceavocado.kernel.operand.collection.Collection;
import com.spaceavocado.kernel.operand.reference.DefaultSerializeOptions;
import com.spaceavocado.kernel.operand.reference.ISerializeOptions;
import com.spaceavocado.kernel.operand.reference.Reference;
import com.spaceavocado.kernel.operand.value.Value;
import com.spaceavocado.kernel.parser.Operator;
import com.spaceavocado.kernel.parser.Parser;

import java.util.HashMap;
import java.util.stream.Stream;
import java.util.regex.Pattern;

public class IllogicalTests {
    private static ISerializeOptions serializeOptions = new DefaultSerializeOptions();
    private static String mockAddress(String value) {
        return serializeOptions.to(value);
    };

    private static Stream<Arguments> parseData() {
        return Stream.of(
            Arguments.of(1, new Value(1)),
            Arguments.of(mockAddress("path"), new Reference("path")),
            Arguments.of(new Object[] { 1 }, new Collection(new IEvaluable[]{ new Value(1) })),
            Arguments.of(new Object[] { Parser.DEFAULT_OPERATOR_MAPPING.get(Operator.EQ), 1, 1 }, new Eq(new Value(1), new Value(1))),
            Arguments.of(new Object[] { Parser.DEFAULT_OPERATOR_MAPPING.get(Operator.AND), true, true }, new And(new IEvaluable[]{ new Value(true), new Value(true) }))
        );
    }

    @ParameterizedTest
    @MethodSource("parseData")
    public void parse(Object input, IEvaluable expected) {
        var illogical = new Illogical();
        var evaluable = illogical.parse(input);
    
        assertInstanceOf(IEvaluable.class, evaluable);
        assertEquals(expected.toString(), evaluable.toString());
    }

    private static Stream<Arguments> evaluateData() {
        return Stream.of(
            Arguments.of(1, 1),
            Arguments.of(mockAddress("path"), "value"),
            Arguments.of(new Object[] { 1 }, new Object[] { 1 }),
            Arguments.of(new Object[] { Parser.DEFAULT_OPERATOR_MAPPING.get(Operator.EQ), 1, 1 }, true),
            Arguments.of(new Object[] { Parser.DEFAULT_OPERATOR_MAPPING.get(Operator.AND), true, false }, false)
        );
    }

    @ParameterizedTest
    @MethodSource("evaluateData")
    public void evaluate(Object input, Object expected) {
        var illogical = new Illogical();

        var context = new HashMap<String, Object>();
        context.put("path", "value");

        var evaluated = illogical.evaluate(input, context);
    
        if (expected instanceof Object[]) {
            assertArrayEquals((Object[])expected, (Object[])evaluated);
        } else {
            assertEquals(expected, evaluated);
        }
    }

    private static Stream<Arguments> simplifyData() {
        return Stream.of(
            Arguments.of(1, 1),
            Arguments.of(mockAddress("path"), "value"),
            Arguments.of(mockAddress("nested.inner"), 2),
            Arguments.of(mockAddress("list[1]"), 3),
            Arguments.of(mockAddress("missing"), new Reference("missing")),
            Arguments.of(new Object[] { 1 }, new Object[] { 1 }),
            Arguments.of(new Object[] { Parser.DEFAULT_OPERATOR_MAPPING.get(Operator.EQ), 1, 1 }, true),
            Arguments.of(new Object[] { Parser.DEFAULT_OPERATOR_MAPPING.get(Operator.AND), true, true }, true),
            Arguments.of(new Object[] { Parser.DEFAULT_OPERATOR_MAPPING.get(Operator.AND), true, mockAddress("missing") }, new Reference("missing"))
        );
    }

    @ParameterizedTest
    @MethodSource("simplifyData")
    public void simplify(Object input, Object expected) {
        var illogical = new Illogical();

        var context = new HashMap<String, Object>();
        context.put("path", "value");
        context.put("list", new Object[] { 1, 3 });

        var inner = new HashMap<String, Object>();
        inner.put("inner", 2);
        context.put("nested", inner);

        var simplified = illogical.simplify(input, context);
    
        if (simplified instanceof IEvaluable evaluable) {
            assertEquals(expected.toString(), evaluable.toString());
        } else if (expected instanceof Object[]) {
            assertArrayEquals((Object[])expected, (Object[])simplified); 
        } else {
            assertEquals(expected, simplified);
        }
    }

    private static Stream<Arguments> statementData() {
        return Stream.of(
            Arguments.of(1, "1"),
            Arguments.of(true, "true"),
            Arguments.of("val", "\"val\""),
            Arguments.of("$refA", "{refA}"),
            Arguments.of(new Object[] { "==", "$refA", "resolvedA" }, "({refA} == \"resolvedA\")"),
            Arguments.of(new Object[] { "AND", new Object[] { "==", 1, 1 }, new Object[] { "!=", 2, 1 } }, "((1 == 1) AND (2 != 1))")
        );
    }

    @ParameterizedTest
    @MethodSource("statementData")
    public void statement(Object input, String expected) {
        var illogical = new Illogical();
        var statement = illogical.statement(input);
    
        assertEquals(expected, statement);
    }

    private static Stream<Arguments> operatorMappingData() {
        return Stream.of(
            Arguments.of(new Object[] { "IS", 1, 1 }, true),
            Arguments.of(new Object[] { "IS", 1, 2 }, false)
        );
    }

    @ParameterizedTest
    @MethodSource("operatorMappingData")
    public void operatorMapping(Object input, Object expected) {
        var operatorMapping = new HashMap<Operator, String>(Parser.DEFAULT_OPERATOR_MAPPING);
        operatorMapping.put(Operator.EQ, "IS");
    
        var illogical = new Illogical(operatorMapping);
        var evaluated = illogical.evaluate(input, null);
    
        assertEquals(expected, evaluated);
    }

    private static Stream<Arguments> serializeOptionsData() {
        return Stream.of(
            Arguments.of("__ref", new Reference("ref")),
            Arguments.of("$ref", new Value("$ref"))
        );
    }

    @ParameterizedTest
    @MethodSource("serializeOptionsData")
    public void serializeOptions(Object input, Object expected) {
        var illogical = new Illogical(new SampleSerializeOptions());

        var parsed = illogical.parse(input);
        var serialized = parsed.serialize();
    
        assertEquals(expected.toString(), parsed.toString());
        assertEquals(input, serialized);
    }

    private static Stream<Arguments> simplifyOptionsData() {
        return Stream.of(
            Arguments.of("$refA", 1),
            Arguments.of("$refB", new Reference("refB")),
            Arguments.of("$ignored", new Reference("ignored"))
        );
    }

    @ParameterizedTest
    @MethodSource("simplifyOptionsData")
    public void simplifyOptions(Object input, Object expected) {
        var illogical = new Illogical(new SampleSimplifyOptions(
            new String[] { "ignored" }, new Pattern[] { Pattern.compile("^refB") }
        ));

        var context = new HashMap<String, Object>();
        context.put("refA", 1);
        context.put("refB", 2);
        context.put("ignored", 3);
        
        var simplified = illogical.simplify(input, context);

        if (simplified instanceof IEvaluable evaluable) {
            assertEquals(expected.toString(), evaluable.toString());
        } else {
            assertEquals(expected, simplified);
        }
    }

    private static Stream<Arguments> escapeCharacterData() {
        return Stream.of(
            Arguments.of(new Object[] { "*AND", 1, 1 }, new Collection(new IEvaluable[]{ new Value("AND"), new Value(1), new Value(1) }))
        );
    }

    @ParameterizedTest
    @MethodSource("escapeCharacterData")
    public void escapeCharacter(Object input, Object expected) {
        var illogical = new Illogical('*');

        var parsed = illogical.parse(input);
        assertEquals(expected.toString(), parsed.toString());
    }
}
