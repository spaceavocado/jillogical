package com.spaceavocado.kernel.operand;

import java.util.HashMap;

import com.spaceavocado.kernel.IEvaluable;
import com.spaceavocado.kernel.Primitive;

public class Value implements IEvaluable {
    private Object value;

    public Value(Object value)
    {
        if (value != null && !Primitive.isPrimitive(value)) {
            throw new IllegalArgumentException(String.format("value %s could be only a primitive type, null, string, number or bool", value.getClass()));
        }
        this.value = value;
    }

    public Object evaluate(HashMap<String, Object> context) {
        return value;
    }

    @Override
    public Object serialize() {
        return value;
    }

    @Override
    public Object simplify(HashMap<String, Object> context) {
        return value;
    }

    @Override
    public String toString() {
        if (value instanceof String || value instanceof Character) {
            return String.format("\"%s\"", value);
        }
        return String.format("%s", value.toString().toLowerCase());
    }
    
}
