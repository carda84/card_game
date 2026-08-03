package com.cardgame.exception;

import org.springframework.http.HttpStatus;

public class InsufficientGoldException extends BusinessException {
    public InsufficientGoldException(String message) {
        super(message, HttpStatus.PAYMENT_REQUIRED);
    }
    public InsufficientGoldException() {
        this("金币不足");
    }
}
