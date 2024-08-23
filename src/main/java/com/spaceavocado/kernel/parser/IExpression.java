package com.spaceavocado.kernel.parser;

import com.spaceavocado.kernel.IEvaluable;

public interface IExpression {
    IEvaluable create(IEvaluable... operands);
}
