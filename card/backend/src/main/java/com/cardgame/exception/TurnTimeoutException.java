package com.cardgame.exception;

import org.springframework.http.HttpStatus;

/** PvP 回合超时异常 */
public class TurnTimeoutException extends BusinessException {
    public TurnTimeoutException(String message) {
        super(message, HttpStatus.REQUEST_TIMEOUT);
    }
    public TurnTimeoutException() {
        this("回合超时");
    }
}
