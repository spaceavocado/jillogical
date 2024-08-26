package com.spaceavocado.jillogical.kernel.expression.comparison;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.spaceavocado.jillogical.kernel.FlattenContext;
import com.spaceavocado.jillogical.kernel.IEvaluable;


public abstract class Comparison implements IEvaluable {
    private String operator;
    private String symbol;
    private IComparison comparison;
    private ArrayList<IEvaluable> operands;

    public Comparison(String operator, String symbol, IComparison comparison, IEvaluable... operands) {
        this.operator = operator;
        this.symbol = symbol;
        this.comparison = comparison;
        this.operands = new ArrayList<>(Arrays.asList(operands));
    }

    @Override
    public Object evaluate(HashMap<String, Object> context) {
        var flattenContext = FlattenContext.create(context);

        try {
            return comparison.evaluate(
                operands.stream()
                    .map(item -> item.evaluate(flattenContext))
                    .toArray()
            );
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    public Object serialize() {
        return Stream.concat(
            Arrays.stream(new Object[] { symbol }),
            operands.stream().map(operand -> operand.serialize())
        ).toArray();
    }

    @Override
    public Object simplify(HashMap<String, Object> context) {
        var flattenContext = FlattenContext.create(context);
    
        var res = new ArrayList<Object>();
        for (IEvaluable operand : operands) {
            var val = operand.simplify(flattenContext);
            if (val instanceof IEvaluable) {
                return this;
            }

            res.add(val);
        }

        return comparison.evaluate(res.toArray());
    }
    
    @Override
    public String toString() {
        var result = String.format("(%s %s", operands.get(0), operator);
        if (operands.size() > 1) { 
            result += String.format(
                " %s",
                operands.subList(1, operands.size()).stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(" "))
            );
        }
        return String.format("%s)", result);
    }

    public static boolean isNumber(Object subject) {
        return subject instanceof Number;
    }

    public static boolean isText(Object subject) {
        return subject instanceof String || subject instanceof Character;
    }
}
