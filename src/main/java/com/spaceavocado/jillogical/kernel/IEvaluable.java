package com.spaceavocado.jillogical.kernel;

import java.util.HashMap;

public interface IEvaluable {
    Object evaluate(HashMap<String, Object> context) throws EvaluateException;
    Object serialize();
    Object simplify(HashMap<String, Object> context);
    String toString();
}
