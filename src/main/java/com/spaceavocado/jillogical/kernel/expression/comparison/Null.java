package com.spaceavocado.jillogical.kernel.expression.comparison;

import com.spaceavocado.jillogical.kernel.IEvaluable;

public class Null extends Comparison {

    public Null(IEvaluable operand) {
        this(operand, "NULL");
    }
    public Null(IEvaluable operand, String symbol) {
        super(
            "<is none>",
            symbol,
            new IComparison() {
                @Override
                public boolean evaluate(Object... operands) {
                    return operands[0] == null;
                }
            },
            operand
        );
    }
}
