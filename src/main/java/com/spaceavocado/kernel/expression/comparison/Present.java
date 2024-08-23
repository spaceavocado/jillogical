package com.spaceavocado.kernel.expression.comparison;

import com.spaceavocado.kernel.IEvaluable;

public class Present extends Comparison {

    public Present(IEvaluable operand) {
        this(operand, "PRESENT");
    }
    public Present(IEvaluable operand, String symbol) {
        super(
            "<is present>",
            symbol,
            new IComparison() {
                @Override
                public boolean evaluate(Object... operands) {
                    return operands[0] != null;
                }
            },
            operand
        );
    }
}
