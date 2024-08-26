package com.spaceavocado.jillogical.kernel.parser;

import com.spaceavocado.jillogical.kernel.IEvaluable;

public interface IExpression {
    IEvaluable create(IEvaluable... operands);
}
