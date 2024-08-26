package com.spaceavocado.jillogical.kernel.expression.logical;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.spaceavocado.jillogical.kernel.IEvaluable;

public abstract class Logical implements IEvaluable {
    private String operator;
    protected String symbol;
    protected ArrayList<IEvaluable> operands;

    public Logical(String operator, String symbol, IEvaluable... operands)
    {
        this.operator = operator;
        this.symbol = symbol;
        this.operands = new ArrayList<>(Arrays.asList(operands));
    }

    @Override
    public abstract Object evaluate(HashMap<String, Object> context);

    @Override
    public Object serialize() {
        return Stream.concat(
            Arrays.stream(new Object[] { symbol }),
            operands.stream().map(operand -> operand.serialize())
        ).toArray();
    }

    @Override
    public abstract Object simplify(HashMap<String, Object> context);
 
    @Override
    public String toString() {
        if (operands.size() == 1) { 
            return String.format("(%s %s)", operator, operands.get(0));
        }

        return String.format("(%s)", operands.stream()
            .map(Object::toString)
            .collect(Collectors.joining(String.format(" %s ", operator)))
        );
    }
}
