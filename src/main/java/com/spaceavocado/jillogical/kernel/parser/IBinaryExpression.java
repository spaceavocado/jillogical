package com.spaceavocado.jillogical.kernel.parser;

import com.spaceavocado.jillogical.kernel.IEvaluable;

public interface IBinaryExpression {
    IEvaluable create(IEvaluable left, IEvaluable right);
}