package com.spaceavocado.jillogical.kernel.parser;

public class UnexpectedExpressionInputException extends RuntimeException {
    public UnexpectedExpressionInputException(String message) {
        super(message);
    }
}
