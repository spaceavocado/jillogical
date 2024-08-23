package com.spaceavocado.kernel.expression.logical;

import java.util.HashMap;

import com.spaceavocado.kernel.IEvaluable;

public class LogicalSample extends Logical {
    public LogicalSample(String operator, String symbol, IEvaluable[] operands) {
        super(operator, symbol, operands);
    }

    @Override
    public Object evaluate(HashMap<String, Object> context) {
        throw new UnsupportedOperationException("Unimplemented method 'evaluate'");
    }

    @Override
    public Object simplify(HashMap<String, Object> context) {
        throw new UnsupportedOperationException("Unimplemented method 'simplify'");
    }
}