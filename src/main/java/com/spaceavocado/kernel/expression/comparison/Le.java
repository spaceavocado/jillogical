package com.spaceavocado.kernel.expression.comparison;

import java.math.BigDecimal;

import com.spaceavocado.kernel.IEvaluable;

public class Le extends Comparison {

    public Le(IEvaluable left, IEvaluable right) {
        this(left, right, "<=");
    }
    public Le(IEvaluable left, IEvaluable right, String symbol) {
        super(
            "<=",
            symbol,
            new IComparison() {
                @Override
                public boolean evaluate(Object... operands) {
                    return isNumber(operands[0]) && isNumber(operands[1])
                        ? BigDecimal.valueOf(((Number)operands[0]).doubleValue())
                            .compareTo(BigDecimal.valueOf(((Number)operands[1]).doubleValue())) <= 0
                        : false;
                }
            },
            left,
            right
        );
    }    
}
