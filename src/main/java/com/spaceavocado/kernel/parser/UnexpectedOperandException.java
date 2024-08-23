package com.spaceavocado.kernel.parser;

public class UnexpectedOperandException extends RuntimeException {
    public UnexpectedOperandException(String message) {
        super(message);
    }
}
