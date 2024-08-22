package com.spaceavocado.kernel;

import java.util.HashMap;

public interface IEvaluable {
    Object evaluate(HashMap<String, Object> context);
    Object serialize();
    Object simplify(HashMap<String, Object> context);
    String toString();
}
