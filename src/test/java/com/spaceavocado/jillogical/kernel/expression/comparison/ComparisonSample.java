package com.spaceavocado.jillogical.kernel.expression.comparison;

import com.spaceavocado.jillogical.kernel.IEvaluable;

public class ComparisonSample extends Comparison {
    public ComparisonSample(String operator, String symbol, IComparison comparison, IEvaluable[] operands) {
        super(operator, symbol, comparison, operands);
    }
}