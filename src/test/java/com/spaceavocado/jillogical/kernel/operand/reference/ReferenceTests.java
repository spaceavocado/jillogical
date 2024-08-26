package com.spaceavocado.jillogical.kernel.operand.reference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;

import org.junit.jupiter.params.provider.MethodSource;

import com.spaceavocado.jillogical.kernel.FlattenContext;
import com.spaceavocado.jillogical.kernel.IEvaluable;

import java.util.stream.Stream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public class ReferenceTests {
    public class SimplifyOptions implements ISimplifyOptions
    {
        private String[] _ignoredPaths;
        public Pattern[] _ignoredPathsRx;

        public SimplifyOptions(String[] ignoredPaths, Pattern[] ignoredPathsRx)
        {
            this._ignoredPaths = ignoredPaths;
            this._ignoredPathsRx = ignoredPathsRx;
        }

        @Override
        public String[] ignoredPaths() {
            return _ignoredPaths;
        }

        @Override
        public Pattern[] ignoredPathsRx() {
            return _ignoredPathsRx;
        }
    }

    private HashMap<String, Object> EXAMPLE_CONTEXT() {
        return FlattenContext.create(
            FlattenContext.toHashMap(Map.of(
                "refA", 1,
                "refB", FlattenContext.toHashMap(Map.of(
                    "refB1", 2,
                    "refB2", "refB1",
                    "refB3", true
                )),
                "refC", "refB1",
                "refD", "refB2",
                "refE", new Object[] { 1, new Object[] { 2, 3, 4 } },
                "refF", "A",
                "refG", "1",
                "refH", "1.1"
            ))
        );
    }

    private static Stream<Arguments> defaultSerializeOptionsFromData() {
        return Stream.of(
            Arguments.of("", null),
            Arguments.of("ref", null),
            Arguments.of("$ref", "ref")
        );
    }

    @ParameterizedTest
    @MethodSource("defaultSerializeOptionsFromData")
    public void defaultSerializeOptionsFrom(String input, Object expected) {
        assertEquals(expected, new DefaultSerializeOptions().from(input));
    }

    private static Stream<Arguments> defaultSerializeOptionsToData() {
        return Stream.of(
            Arguments.of("ref", "$ref")
        );
    }

    @ParameterizedTest
    @MethodSource("defaultSerializeOptionsToData")
    public void defaultSerializeOptionsTo(String input, Object expected) {
        assertEquals(expected, new DefaultSerializeOptions().to(input));
    }

    private static Stream<Arguments> isIgnoredPathData() {
        return Stream.of(
            Arguments.of("path", null, null, false),
            Arguments.of("ignored", new String[]{ "bogus", "ignored" }, null, true),
            Arguments.of("not", new String[] { "ignored" }, null, false),
            Arguments.of("refC", null, new Pattern[] { Pattern.compile("^ref") }, true)
        );
    }

    @ParameterizedTest
    @MethodSource("isIgnoredPathData")
    public void isIgnoredPath(String path, String[] ignoredPaths, Pattern[] ignoredPathsRx, boolean expected) {
        assertEquals(expected, Reference.isIgnoredPath(path, ignoredPaths, ignoredPathsRx));
    }

    private static Stream<Arguments> getDataTypeData() {
        return Stream.of(
            Arguments.of("ref", DataType.Undefined),
            Arguments.of("ref.(X)", DataType.Undefined),
            Arguments.of("ref.(Bogus)", DataType.Unsupported),
            Arguments.of("ref.(String)", DataType.String),
            Arguments.of("ref.(Number)", DataType.Number),
            Arguments.of("ref.(Integer)", DataType.Integer),
            Arguments.of("ref.(Float)", DataType.Float),
            Arguments.of("ref.(Boolean)", DataType.Boolean)
        );
    }

    @ParameterizedTest
    @MethodSource("getDataTypeData")
    public void getDataType(String input, DataType expected) {
        assertEquals(expected, Reference.getDataType(input));
    }

    private static Stream<Arguments> trimDataTypeData() {
        return Stream.of(
            Arguments.of("ref", "ref"),
            Arguments.of("ref.(X)", "ref.(X)"),
            Arguments.of("ref.(String)", "ref")
        );
    }

    @ParameterizedTest
    @MethodSource("trimDataTypeData")
    public void trimDataType(String input, String expected) {
        assertEquals(expected, Reference.trimDataType(input));
    }

    private static Stream<Arguments> contextLookupData() {
        return Stream.of(
            Arguments.of("UNDEFINED", false, "UNDEFINED", null),
            Arguments.of("refA", true, "refA", 1),
            Arguments.of("refB.refB1", true, "refB.refB1", 2),
            Arguments.of("refB.{refC}", true, "refB.refB1", 2),
            Arguments.of("refB.{UNDEFINED}", false, "refB.{UNDEFINED}", null),
            Arguments.of("refB.{refB.refB2}", true, "refB.refB1", 2),
            Arguments.of("refB.{refB.{refD}}", true, "refB.refB1", 2),
            Arguments.of("refE[0]", true, "refE[0]", 1),
            Arguments.of("refE[2]", false, "refE[2]", null),
            Arguments.of("refE[1][0]", true, "refE[1][0]", 2),
            Arguments.of("refE[1][3]", false, "refE[1][3]", null),
            Arguments.of("refE[{refA}][0]", true, "refE[1][0]", 2),
            Arguments.of("refE[{refA}][{refB.refB1}]", true, "refE[1][2]", 4),
            Arguments.of("ref{refF}", true, "refA", 1),
            Arguments.of("ref{UNDEFINED}", false, "ref{UNDEFINED}", null)
        );
    }

    @ParameterizedTest
    @MethodSource("contextLookupData")
    public void contextLookup(String path, boolean expectedFound, String expectedPath, Object expectedValue) {
        var lookup = Reference.contextLookup(EXAMPLE_CONTEXT(), path);

        assertEquals(expectedFound, lookup.a);
        assertEquals(expectedPath, lookup.b);
        assertEquals(expectedValue, lookup.c);
    }

    private static Stream<Arguments> evaluateData() {
        return Stream.of(
            Arguments.of("refA", DataType.Integer, 1),
            Arguments.of("refA", DataType.String, "1"),
            Arguments.of("refG", DataType.Number, 1),
            Arguments.of("refH", DataType.Float, 1.1f),
            Arguments.of("refB.refB3", DataType.String, "true"),
            Arguments.of("refB.refB3", DataType.Boolean, true),
            Arguments.of("refB.refB3", DataType.Number, 1),
            Arguments.of("refJ", DataType.Undefined, null)
        );
    }

    @ParameterizedTest
    @MethodSource("evaluateData")
    public void evaluate(String path, DataType dataType, Object expected) {
        var lookup = Reference.evaluate(EXAMPLE_CONTEXT(), path, dataType);

        assertEquals(expected, lookup.c);
    }

    private static Stream<Arguments> evaluateOperandData() {
        return Stream.of(
            Arguments.of("refA", 1),
            Arguments.of("refB.refB3", true),
            Arguments.of("refE[1][2]", 4),
            Arguments.of("refJ", null)
        );
    }

    @ParameterizedTest
    @MethodSource("evaluateOperandData")
    public void evaluateOperand(String path, Object expected) {
        var value = new Reference(path).evaluate(EXAMPLE_CONTEXT());

        assertEquals(expected, value);
    }

    private static Stream<Arguments> serializeData() {
        return Stream.of(
            Arguments.of("ref", "$ref"),
            Arguments.of("ref.(Number)", "$ref.(Number)")
        );
    }

    @ParameterizedTest
    @MethodSource("serializeData")
    public void serialize(String path, Object expected) {
        var value = new Reference(path).serialize();

        assertEquals(expected, value);
    }

    private static Stream<Arguments> simplifyData() {
        return Stream.of(
            Arguments.of("refA", 1),
            Arguments.of("ignored", new Reference("ignored")),
            Arguments.of("refB.refB1", new Reference("refB.refB1")),
            Arguments.of("ref", new Reference("ref"))
        );
    }

    @ParameterizedTest
    @MethodSource("simplifyData")
    public void simplify(String path, Object expected) {
        var simplifyOptions = new SimplifyOptions(new String[] { "ignored" }, new Pattern[] { Pattern.compile("^refB") });
        var operand = new Reference(path, simplifyOptions);
        var simplified = operand.simplify(EXAMPLE_CONTEXT());

        if (simplified instanceof IEvaluable) {
            assertEquals(expected.toString(), simplified.toString());
        } else {
            assertEquals(simplified, expected);
        } 
    }

    private static Stream<Arguments> toNumberData() {
        return Stream.of(
            Arguments.of(1, 1),
            Arguments.of(1.1, 1.1),
            Arguments.of(1.1f, 1.1f),
            Arguments.of("1", 1),
            Arguments.of("1.1", 1.1f),
            Arguments.of("1.9", 1.9f),
            Arguments.of(true, 1),
            Arguments.of(false, 0)
        );
    }

    @ParameterizedTest
    @MethodSource("toNumberData")
    public void toNumber(Object input, Number expected) {
        assertEquals(expected, Reference.toNumber(input));
    }

    private static Stream<Arguments> toNumberExceptionData() {
        return Stream.of(
            Arguments.of("bogus"),
            Arguments.of(new HashMap<String, Object>())
        );
    }

    @ParameterizedTest
    @MethodSource("toNumberExceptionData")
    public void toNumberException(Object input) {
        assertThrows(ClassCastException.class, () -> Reference.toNumber(input));
    }

    private static Stream<Arguments> toIntegerData() {
        return Stream.of(
            Arguments.of(1, 1),
            Arguments.of(1.1, 1),
            Arguments.of(1.1f, 1),
            Arguments.of("1", 1),
            Arguments.of("1.1", 1),
            Arguments.of("1.9", 1),
            Arguments.of(true, 1),
            Arguments.of(false, 0)
        );
    }

    @ParameterizedTest
    @MethodSource("toIntegerData")
    public void toInteger(Object input, int expected) {
        assertEquals(expected, Reference.toInteger(input));
    }

    private static Stream<Arguments> toIntegerExceptionData() {
        return Stream.of(
            Arguments.of("bogus"),
            Arguments.of(new HashMap<String, Object>())
        );
    }

    @ParameterizedTest
    @MethodSource("toIntegerExceptionData")
    public void toIntegerException(Object input) {
        assertThrows(ClassCastException.class, () -> Reference.toInteger(input));
    }

    private static Stream<Arguments> toFloatData() {
        return Stream.of(
            Arguments.of(1, 1.0f),
            Arguments.of(1.1, 1.1f),
            Arguments.of(1.1f, 1.1f),
            Arguments.of("1", 1.0f),
            Arguments.of("1.1", 1.1f),
            Arguments.of("1.9", 1.9f)
        );
    }

    @ParameterizedTest
    @MethodSource("toFloatData")
    public void toFloat(Object input, float expected) {
        assertEquals(expected, Reference.toFloat(input));
    }

    private static Stream<Arguments> toFloatExceptionData() {
        return Stream.of(
            Arguments.of("bogus"),
            Arguments.of(new HashMap<String, Object>())
        );
    }

    @ParameterizedTest
    @MethodSource("toFloatExceptionData")
    public void toFloatException(Object input) {
        assertThrows(ClassCastException.class, () -> Reference.toFloat(input));
    }

    private static Stream<Arguments> toStringData() {
        return Stream.of(
            Arguments.of(1, "1"),
            Arguments.of(1f, "1.0"),
            Arguments.of(1d, "1.0"),
            Arguments.of(1.1, "1.1"),
            Arguments.of("1", "1"),
            Arguments.of(true, "true"),
            Arguments.of(false, "false")
        );
    }

    @ParameterizedTest
    @MethodSource("toStringData")
    public void toString(Object input, String expected) {
        assertEquals(expected, Reference.toString(input));
    }

    private static Stream<Arguments> toBooleanData() {
        return Stream.of(
            Arguments.of(true, true),
            Arguments.of(false, false),
            Arguments.of("true", true),
            Arguments.of("false", false),
            Arguments.of("True", true),
            Arguments.of("False", false),
            Arguments.of("1", true),
            Arguments.of("0", false),
            Arguments.of(1, true),
            Arguments.of(0, false)
        );
    }

    @ParameterizedTest
    @MethodSource("toBooleanData")
    public void toBoolean(Object input, boolean expected) {
        assertEquals(expected, Reference.toBoolean(input));
    }

    private static Stream<Arguments> toBooleanExceptionData() {
        return Stream.of(
            Arguments.of(1.1f),
            Arguments.of(3),
            Arguments.of("bogus"),
            Arguments.of(new HashMap<String, Object>())
        );
    }

    @ParameterizedTest
    @MethodSource("toBooleanExceptionData")
    public void toBooleanException(Object input) {
        assertThrows(ClassCastException.class, () -> Reference.toBoolean(input));
    }

    private static Stream<Arguments> stringifyData() {
        return Stream.of(
            Arguments.of("ref", "{ref}"),
            Arguments.of("ref.(Number)", "{ref.(Number)}")
        );
    }

    @ParameterizedTest
    @MethodSource("stringifyData")
    public void stringify(String address, String expected) {
        assertEquals(expected, new Reference(address).toString());
    }
}
