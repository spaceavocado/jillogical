package com.spaceavocado;

import com.spaceavocado.kernel.operand.reference.ISerializeOptions;

public class SampleSerializeOptions implements ISerializeOptions
{
    public String from(String operand) {
        return operand.length() > 2 && operand.startsWith("__")
            ? operand.substring(2)
            : null;
    }

    public String to(String operand) {
        return String.format("__%s", operand);
    }
}
