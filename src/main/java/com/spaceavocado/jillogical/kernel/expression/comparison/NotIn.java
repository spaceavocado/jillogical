package com.spaceavocado.jillogical.kernel.expression.comparison;

import java.util.Arrays;
import java.util.Objects;

import com.spaceavocado.jillogical.kernel.IEvaluable;

public class NotIn extends Comparison {

    public NotIn(IEvaluable left, IEvaluable right) {
        this(left, right, "NOT IN");
    }
    public NotIn(IEvaluable left, IEvaluable right, String symbol) {
        super(
            "<not in>",
            symbol,
            new IComparison() {
                @Override
                public boolean evaluate(Object... operands) {
                    var leftIsEnumerable = operands[0] instanceof Object[];
                    var rightIsEnumerable = operands[1] instanceof Object[];

                    if ((leftIsEnumerable && rightIsEnumerable) || (!leftIsEnumerable && !rightIsEnumerable)) {
                        return true;
                    }
                    
                    return leftIsEnumerable
                        ? !Arrays.stream((Object[])operands[0]).anyMatch((item) -> Objects.equals(item, operands[1]))
                        : !Arrays.stream((Object[])operands[1]).anyMatch((item) ->  Objects.equals(item, operands[0]));
                }
            },
            left,
            right
        );
    }
}
