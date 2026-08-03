package com.cardgame.exception;

import org.springframework.http.HttpStatus;

/** 卡组校验失败异常 */
public class DeckValidationException extends BusinessException {
    public DeckValidationException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
