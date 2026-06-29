package com.fynxt.orderbook.exception;

public class InsufficientSharesException extends RuntimeException {
    public InsufficientSharesException(String message) {
        super(message);
    }
}
