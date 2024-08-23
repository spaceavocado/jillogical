package com.spaceavocado.kernel.parser;

import com.spaceavocado.kernel.IEvaluable;

public interface IBinaryExpression {
    IEvaluable create(IEvaluable left, IEvaluable right);
}