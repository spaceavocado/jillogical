package com.spaceavocado.jillogical.kernel.parser;

import com.spaceavocado.jillogical.kernel.IEvaluable;

public interface IUnaryExpression {
    IEvaluable create(IEvaluable operand);
}
