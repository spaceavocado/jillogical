package com.spaceavocado.kernel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.stream.Stream;
import java.util.Map;

public class FlattenContextTests {
    private static Stream<Arguments> flattenContextData() {
        return Stream.of(
            Arguments.of(null, null),
            Arguments.of(FlattenContext.toFlattenContext(Map.of("a", 1)), FlattenContext.toFlattenContext(Map.of("a", 1))),
            Arguments.of(FlattenContext.toHashMap(Map.of("a", 1)), FlattenContext.toFlattenContext(Map.of("a", 1))),
            Arguments.of(
                FlattenContext.toHashMap(Map.of("a", 1, "b", FlattenContext.toHashMap(Map.of("c", 5, "d", true)), "c", 1.1)),
                FlattenContext.toFlattenContext(Map.of("a", 1, "b.c", 5, "b.d", true, "c", 1.1))
            ),
            Arguments.of(
                FlattenContext.toHashMap(Map.of("a", 1, "b", new Object[] { 1, "val", true })),
                FlattenContext.toFlattenContext(Map.of("a", 1, "b[0]", 1, "b[1]", "val", "b[2]", true))
            ),
            Arguments.of(
                FlattenContext.toHashMap(Map.of("a", 1, "b", new Object[] { 1.1, FlattenContext.toHashMap(Map.of("c", false, "d", 1.2)), 'c' })),
                FlattenContext.toFlattenContext(Map.of("a", 1, "b[0]", 1.1, "b[1].c", false, "b[1].d", 1.2, "b[2]", 'c'))
            )
        );
    }

    @ParameterizedTest
    @MethodSource("flattenContextData")
    public void flattenContext(HashMap<String, Object> input, HashMap<String, Object> expected) {
        assertEquals(expected, FlattenContext.create(input));
    }
}
