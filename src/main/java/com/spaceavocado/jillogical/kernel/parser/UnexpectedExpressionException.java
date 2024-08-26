package com.spaceavocado.jillogical.kernel.parser;

public class UnexpectedExpressionException extends RuntimeException {
    public UnexpectedExpressionException(String message) {
        super(message);
    }
}