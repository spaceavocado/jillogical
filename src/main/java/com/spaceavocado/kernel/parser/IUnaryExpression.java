package com.spaceavocado.kernel.parser;

import com.spaceavocado.kernel.IEvaluable;

public interface IUnaryExpression {
    IEvaluable create(IEvaluable operand);
}
