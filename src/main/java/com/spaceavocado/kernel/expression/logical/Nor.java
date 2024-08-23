package com.spaceavocado.kernel.expression.logical;

import java.util.ArrayList;
import java.util.HashMap;

import com.spaceavocado.kernel.EvaluateException;
import com.spaceavocado.kernel.FlattenContext;
import com.spaceavocado.kernel.IEvaluable;

public class Nor extends Logical {
    private String notSymbol;

    public Nor(IEvaluable[] operands) {
        this(operands, "NOR", "NOT");
    }
    public Nor(IEvaluable[] operands, String symbol, String notSymbol)
    {
        super("NOR", symbol, operands);

        if (operands.length < 2) {
            throw new IllegalArgumentException("Non unary logical expression must have at least 2 operands");
        }

        this.notSymbol = notSymbol;
    }

    @Override
    public Object evaluate(HashMap<String, Object> context) {
        var flattenContext = FlattenContext.create(context);
    
        for (IEvaluable operand : operands) {
            var res = operand.evaluate(flattenContext);
            if (!(res instanceof Boolean)) {
                throw new EvaluateException(String.format("invalid evaluated operand \"%s\" (%s) in OR expression, must be boolean value", res, operand));
            } else if ((Boolean)res) {
                return false;
            }
        }

        return true;
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
                    return false;
                }
                continue;
            }

            simplified.add(res instanceof IEvaluable ? (IEvaluable)res : operand);
        }

        if (simplified.size() == 0) {
            return true;
        }

        if (simplified.size() == 1) {
            return new Not(simplified.get(0), notSymbol);
        }

        return new Nor(simplified.toArray(new IEvaluable[0]), symbol, notSymbol);
    }
}
