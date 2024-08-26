package com.spaceavocado.jillogical.kernel;

public final class Primitive
{
    public static boolean isPrimitive(Object value) {
        return
            value instanceof String ||
            value instanceof Character ||
            value instanceof Number ||
            value instanceof Boolean;
    }

}