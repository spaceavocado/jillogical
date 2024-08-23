package com.spaceavocado.kernel.parser;

public class UnexpectedExpressionInputException extends RuntimeException {
    public UnexpectedExpressionInputException(String message) {
        super(message);
    }
}
