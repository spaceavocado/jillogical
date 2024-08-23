package com.spaceavocado.kernel.operand.reference;

public class DefaultSerializeOptions implements ISerializeOptions
{
    public String From(String operand) {
        return operand.length() > 1 && operand.startsWith("$")
            ? operand.substring(1)
            : null;
    }

    public String To(String operand) {
        return String.format("$%s", operand);
    };
}
