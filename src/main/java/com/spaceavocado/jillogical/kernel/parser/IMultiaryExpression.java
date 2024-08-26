package com.spaceavocado.jillogical.kernel.parser;

import com.spaceavocado.jillogical.kernel.IEvaluable;

public interface IMultiaryExpression {
    IEvaluable create(IEvaluable... operands);
}