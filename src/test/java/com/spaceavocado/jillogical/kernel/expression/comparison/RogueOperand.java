package com.spaceavocado.jillogical.kernel.expression.comparison;

import java.util.HashMap;

import com.spaceavocado.jillogical.kernel.IEvaluable;

public class RogueOperand implements IEvaluable{

    @Override
    public Object evaluate(HashMap<String, Object> context) {
        throw new UnsupportedOperationException("Unimplemented method 'evaluate'");
    }

    @Override
    public Object serialize() {
        throw new UnsupportedOperationException("Unimplemented method 'serialize'");
    }

    @Override
    public Object simplify(HashMap<String, Object> context) {
        throw new UnsupportedOperationException("Unimplemented method 'simplify'");
    }
    
}
