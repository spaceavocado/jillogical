package com.spaceavocado.jillogical.kernel.expression.logical;

import java.util.HashMap;

import com.spaceavocado.jillogical.kernel.EvaluateException;
import com.spaceavocado.jillogical.kernel.FlattenContext;
import com.spaceavocado.jillogical.kernel.IEvaluable;

public class Not extends Logical {
    public Not(IEvaluable operand) {
        this(operand, "NOT");
    }
    public Not(IEvaluable operand, String symbol)
    {
        super("NOT", symbol, operand);
    }

    @Override
    public Object evaluate(HashMap<String, Object> context) {
        var flattenContext = FlattenContext.create(context);
    
        var operand = operands.get(0);
        var res = operand.evaluate(flattenContext);
        if (!(res instanceof Boolean)) {
            throw new EvaluateException(String.format("invalid evaluated operand \"%s\" (%s) in NOT expression, must be boolean value", res, operand));
        }

        return !(Boolean)res;
    }

    @Override
    public Object simplify(HashMap<String, Object> context) {
        var flattenContext = FlattenContext.create(context);
    
        var operand = operands.get(0);
        var res = operand.simplify(flattenContext);
        if (res instanceof Boolean) {
            return !(Boolean)res;
        }

        return this;
    }
}
