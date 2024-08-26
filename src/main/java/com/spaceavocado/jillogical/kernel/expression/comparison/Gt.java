package com.spaceavocado.jillogical.kernel.expression.comparison;

import java.math.BigDecimal;

import com.spaceavocado.jillogical.kernel.IEvaluable;

public class Gt extends Comparison {

    public Gt(IEvaluable left, IEvaluable right) {
        this(left, right, ">");
    }
    public Gt(IEvaluable left, IEvaluable right, String symbol) {
        super(
            ">",
            symbol,
            new IComparison() {
                @Override
                public boolean evaluate(Object... operands) {
                    return isNumber(operands[0]) && isNumber(operands[1])
                        ? BigDecimal.valueOf(((Number)operands[0]).doubleValue())
                            .compareTo(BigDecimal.valueOf(((Number)operands[1]).doubleValue())) > 0
                        : false;
                }
            },
            left,
            right
        );
    }    
}
