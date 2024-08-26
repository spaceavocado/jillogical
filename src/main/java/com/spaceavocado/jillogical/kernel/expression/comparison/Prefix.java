package com.spaceavocado.jillogical.kernel.expression.comparison;

import com.spaceavocado.jillogical.kernel.IEvaluable;

public class Prefix extends Comparison {

    public Prefix(IEvaluable left, IEvaluable right) {
        this(left, right, "PREFIX");
    }
    public Prefix(IEvaluable left, IEvaluable right, String symbol) {
        super(
            "<prefixes>",
            symbol,
            new IComparison() {
                @Override
                public boolean evaluate(Object... operands) {
                    return isText(operands[0]) && isText(operands[1])
                        ? operands[1].toString().startsWith(operands[0].toString())
                        : false;
                }
            },
            left,
            right
        );
    }
}
