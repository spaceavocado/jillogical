package com.spaceavocado.kernel.parser;

import com.spaceavocado.kernel.IEvaluable;

public interface IMultiaryExpression {
    IEvaluable create(IEvaluable... operands);
}