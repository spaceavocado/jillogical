package com.spaceavocado.jillogical.kernel.expression.comparison;

import com.spaceavocado.jillogical.kernel.IEvaluable;

public class Suffix extends Comparison {

    public Suffix(IEvaluable left, IEvaluable right) {
        this(left, right, "SUFFIX");
    }
    public Suffix(IEvaluable left, IEvaluable right, String symbol) {
        super(
            "<with suffix>",
            symbol,
            new IComparison() {
                @Override
                public boolean evaluate(Object... operands) {
                    return isText(operands[0]) && isText(operands[1])
                        ? operands[0].toString().endsWith(operands[1].toString())
                        : false;
                }
            },
            left,
            right
        );
    }
}
