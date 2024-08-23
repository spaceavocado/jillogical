package com.spaceavocado.kernel.expression.comparison;

import java.util.Arrays;
import java.util.Objects;

import com.spaceavocado.kernel.IEvaluable;

public class In extends Comparison {

    public In(IEvaluable left, IEvaluable right) {
        this(left, right, "IN");
    }
    public In(IEvaluable left, IEvaluable right, String symbol) {
        super(
            "<in>",
            symbol,
            new IComparison() {
                @Override
                public boolean evaluate(Object... operands) {
                    var leftIsEnumerable = operands[0] instanceof Object[];
                    var rightIsEnumerable = operands[1] instanceof Object[];

                    if ((leftIsEnumerable && rightIsEnumerable) || (!leftIsEnumerable && !rightIsEnumerable)) {
                        return false;
                    }
                    
                    return leftIsEnumerable
                        ? Arrays.stream((Object[])operands[0]).anyMatch((item) -> Objects.equals(item, operands[1]))
                        : Arrays.stream((Object[])operands[1]).anyMatch((item) ->  Objects.equals(item, operands[0]));
                }
            },
            left,
            right
        );
    }
}
