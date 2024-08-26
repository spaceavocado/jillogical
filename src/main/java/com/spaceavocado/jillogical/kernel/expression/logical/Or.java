package com.spaceavocado.jillogical.kernel.expression.logical;

import java.util.ArrayList;
import java.util.HashMap;

import com.spaceavocado.jillogical.kernel.EvaluateException;
import com.spaceavocado.jillogical.kernel.FlattenContext;
import com.spaceavocado.jillogical.kernel.IEvaluable;

public class Or extends Logical {
    public Or(IEvaluable[] operands) {
        this(operands, "OR");
    }
    public Or(IEvaluable[] operands, String symbol)
    {
        super("OR", symbol, operands);

        if (operands.length < 2) {
            throw new IllegalArgumentException("Non unary logical expression must have at least 2 operands");
        }
    }

    @Override
    public Object evaluate(HashMap<String, Object> context) {
        var flattenContext = FlattenContext.create(context);
    
        for (IEvaluable operand : operands) {
            var res = operand.evaluate(flattenContext);
            if (!(res instanceof Boolean)) {
                throw new EvaluateException(String.format("invalid evaluated operand \"%s\" (%s) in OR expression, must be boolean value", res, operand));
            } else if ((Boolean)res) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Object simplify(HashMap<String, Object> context) {
        var flattenContext = FlattenContext.create(context);
        var simplified = new ArrayList<IEvaluable>();

        for (IEvaluable operand : operands)
        {
            var res = operand.simplify(flattenContext);
            if (res instanceof Boolean) {
                if ((Boolean)res) {
                    return true;
                }
                continue;
            }

            simplified.add(res instanceof IEvaluable ? (IEvaluable)res : operand);
        }

        if (simplified.size() == 0) {
            return false;
        }

        if (simplified.size() == 1) {
            return simplified.get(0);
        }

        return new Or(simplified.toArray(new IEvaluable[0]), symbol);
    }
}
