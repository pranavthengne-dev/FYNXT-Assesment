package com.fynxt.orderbook.exception;

public class PendingOrderLimitExceededException extends RuntimeException {
    public PendingOrderLimitExceededException(String message) {
        super(message);
    }
}
