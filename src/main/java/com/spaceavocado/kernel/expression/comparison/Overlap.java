package com.spaceavocado.kernel.expression.comparison;

import java.util.Objects;

import com.spaceavocado.kernel.IEvaluable;

public class Overlap extends Comparison {

    public Overlap(IEvaluable left, IEvaluable right) {
        this(left, right, "OVERLAP");
    }
    public Overlap(IEvaluable left, IEvaluable right, String symbol) {
        super(
            "<overlaps>",
            symbol,
            new IComparison() {
                @Override
                public boolean evaluate(Object... operands) {
                    var left = operands[0] instanceof Object[]
                        ? (Object[])operands[0]
                        : new Object[] { operands[0] };

                    var right = operands[1] instanceof Object[]
                        ? (Object[])operands[1]
                        : new Object[] { operands[1] };

                    for (Object i : left) {
                        for (Object j : right) {
                            if (Objects.equals(i, j)) {
                                return true;
                            }
                        }
                    }

                    return false;
                }
            },
            left,
            right
        );
    }
}
