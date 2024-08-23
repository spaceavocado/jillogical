package com.spaceavocado.kernel.operand.reference;

public class DefaultSerializeOptions implements ISerializeOptions
{
    public String from(String operand) {
        return operand.length() > 1 && operand.startsWith("$")
            ? operand.substring(1)
            : null;
    }

    public String to(String operand) {
        return String.format("$%s", operand);
    };
}
