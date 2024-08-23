package com.spaceavocado.kernel.operand.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import org.junit.jupiter.params.provider.Arguments;

import com.spaceavocado.kernel.FlattenContext;
import com.spaceavocado.kernel.IEvaluable;
import com.spaceavocado.kernel.operand.value.Value;
import com.spaceavocado.kernel.operand.reference.Reference;

import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashSet;
import java.util.Map;
import java.util.stream.Stream;

public class CollectionTests {
    @Test
    public void invalidItems() {
        assertThrows(IllegalArgumentException.class, () -> new Collection(new IEvaluable[] {}));
    }

    private static Stream<Arguments> evaluateData() {
        return Stream.of(
            Arguments.of(new IEvaluable[] { new Value(1) }, new Object[] { 1 }),
            Arguments.of(new IEvaluable[] { new Value("1") }, new Object[] { "1" }),
            Arguments.of(new IEvaluable[] { new Value(true) }, new Object[] { true }),
            Arguments.of(new IEvaluable[] { new Reference("RefA") }, new Object[] { "A" }),
            Arguments.of(new IEvaluable[] { new Value(1), new Reference("RefA") }, new Object[] { 1, "A" })
        );
    }

    @ParameterizedTest
    @MethodSource("evaluateData")
    public void evaluate(IEvaluable[] items, Object[] expected) {
        var collection = new Collection(items);
        var evaluated = collection.evaluate(FlattenContext.toHashMap(Map.of("RefA", "A")));

        assertArrayEquals(expected, (Object[])evaluated);
    }

    private static Stream<Arguments> serializeData() {
        return Stream.of(
            Arguments.of(new IEvaluable[] { new Value(1) }, new Object[] { 1 }),
            Arguments.of(new IEvaluable[] { new Value("1") }, new Object[] { "1" }),
            Arguments.of(new IEvaluable[] { new Value(true) }, new Object[] { true }),
            Arguments.of(new IEvaluable[] { new Reference("RefA") }, new Object[] { "$RefA" }),
            Arguments.of(new IEvaluable[] { new Value("=="), new Value(1), new Value(1) }, new Object[] { "\\==", 1, 1 }),
            Arguments.of(new IEvaluable[] { new Value("!="), new Value(1), new Value(1) }, new Object[] { "!=", 1, 1 })
        );
    }

    @ParameterizedTest
    @MethodSource("serializeData")
    public void serialize(IEvaluable[] items, Object[] expected) {
        var escapedOperators = new HashSet<String>();
        escapedOperators.add("==");
        var collection = new Collection(items, escapedOperators);

        assertArrayEquals(expected, (Object[])collection.serialize());
    }

    private static Stream<Arguments> simplifyData() {
        return Stream.of(
            Arguments.of(
                new IEvaluable[] { new Reference("RefB") },
                new Collection(new IEvaluable[] { new Reference("RefB") })
            ),
            Arguments.of(
                new IEvaluable[] { new Reference("RefA") },
                new Object[] { "A" }
            ),
            Arguments.of(
                new IEvaluable[] { new Value(1), new Reference("RefA") },
                new Object[] { 1, "A" }
            ),
            Arguments.of(
                new IEvaluable[] { new Reference("RefA"), new Reference("RefB")  },
                new Collection(new IEvaluable[] { new Reference("RefA"), new Reference("RefB") })
            )
        );
    }

    @ParameterizedTest
    @MethodSource("simplifyData")
    public void simplify(IEvaluable[] items, Object expected) {
        var collection = new Collection(items);
        var simplified = collection.simplify(FlattenContext.toHashMap(Map.of("RefA", "A")));

        if (simplified instanceof IEvaluable evaluable) {
            assertEquals(expected.toString(), evaluable.toString());
        } else {
            assertArrayEquals((Object[])expected, (Object[])simplified);
        }
    }

    private static Stream<Arguments> shouldBeEscapedData() {
        return Stream.of(
            Arguments.of("==", true),
            Arguments.of("!=", false),
            Arguments.of(null, false),
            Arguments.of(true, false)
        );
    }

    @ParameterizedTest
    @MethodSource("shouldBeEscapedData")
    public void shouldBeEscaped(Object subject, boolean expected) {
        var escapedOperators = new HashSet<String>();
        escapedOperators.add("==");

        assertEquals(expected, Collection.shouldBeEscaped(subject, escapedOperators));
    }

    private static Stream<Arguments> escapeOperatorData() {
        return Stream.of(
            Arguments.of("==", '\\', "\\=="),
            Arguments.of("==", 'g', "g==")
        );
    }

    @ParameterizedTest
    @MethodSource("escapeOperatorData")
    public void escapeOperator(String subject, char escapeOperator, String expected) {
        assertEquals(expected, Collection.escapeOperator(subject, escapeOperator));
    }

    private static Stream<Arguments> toStringData() {
        return Stream.of(
            Arguments.of(new IEvaluable[] { new Value(1) }, "[1]"),
            Arguments.of(new IEvaluable[] { new Value("1") }, "[\"1\"]"),
            Arguments.of(new IEvaluable[] { new Value(true) }, "[true]"),
            Arguments.of(new IEvaluable[] { new Reference("RefA") }, "[{RefA}]"),
            Arguments.of(new IEvaluable[] { new Value(1), new Reference("RefA") }, "[1, {RefA}]")
        );
    }

    @ParameterizedTest
    @MethodSource("toStringData")
    public void toString(IEvaluable[] items, String expected) {
        var collection = new Collection(items);
        assertEquals(expected, collection.toString());
    }
}
