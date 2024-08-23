package com.spaceavocado.kernel.expression.comparison;

import java.util.Objects;

import com.spaceavocado.kernel.IEvaluable;

public class Ne extends Comparison {

    public Ne(IEvaluable left, IEvaluable right) {
        this(left, right, "!=");
    }
    public Ne(IEvaluable left, IEvaluable right, String symbol) {
        super(
            "!=",
            symbol,
            new IComparison() {
                @Override
                public boolean evaluate(Object... operands) {
                    return !Objects.equals(operands[0], operands[1]);
                }
            },
            left,
            right
        );
    }
}
