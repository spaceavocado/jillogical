package com.spaceavocado.jillogical.kernel.expression.logical;

import java.util.ArrayList;
import java.util.HashMap;

import com.spaceavocado.jillogical.kernel.EvaluateException;
import com.spaceavocado.jillogical.kernel.FlattenContext;
import com.spaceavocado.jillogical.kernel.IEvaluable;

public class Xor extends Logical {
    private String notSymbol;
    private String norSymbol;

    public Xor(IEvaluable[] operands) {
        this(operands, "XOR", "NOT", "NOR");
    }
    public Xor(IEvaluable[] operands, String symbol, String notSymbol, String norSymbol)
    {
        super("XOR", symbol, operands);

        if (operands.length < 2) {
            throw new IllegalArgumentException("Non unary logical expression must have at least 2 operands");
        }

        this.notSymbol = notSymbol;
        this.norSymbol = norSymbol;
    }

    @Override
    public Object evaluate(HashMap<String, Object> context) {
        var flattenContext = FlattenContext.create(context);
        Boolean xor = null;
    
        for (IEvaluable operand : operands) {
            var res = operand.evaluate(flattenContext);
            if (!(res instanceof Boolean)) {
                throw new EvaluateException(String.format("invalid evaluated operand \"%s\" (%s) in OR expression, must be boolean value", res, operand));
            }

            if (xor == null) {
                xor = (Boolean)res;
                continue;
            }

            if ((Boolean)xor && (Boolean)res) {
                return false;
            }

            xor = (Boolean)res ? true : xor;
        }

        return xor != null ? xor : false;
    }

    @Override
    public Object simplify(HashMap<String, Object> context) {
        var flattenContext = FlattenContext.create(context);
        var truthy = 0;
        var simplified = new ArrayList<IEvaluable>();

        for (IEvaluable operand : operands)
        {
            var res = operand.simplify(flattenContext);
            if (res instanceof Boolean) {
                if ((Boolean)res) {
                    truthy += 1;
                }
                if (truthy > 1) {
                    return false;
                }
                continue;
            }

            simplified.add(res instanceof IEvaluable ? (IEvaluable)res : operand);
        }

        if (simplified.size() == 0) {
            return truthy == 1;
        }

        if (simplified.size() == 1) {
            if (truthy == 1)
            {
                return new Not(simplified.get(0), notSymbol);
            }

            return simplified.get(0);
        }

        if (truthy == 1) {
            return new Nor(simplified.toArray(new IEvaluable[0]), norSymbol, notSymbol);
        }

        return new Xor(simplified.toArray(new IEvaluable[0]), symbol, notSymbol, norSymbol);
    }
}
