package com.cardgame.exception;

import org.springframework.http.HttpStatus;

/** 无法匹配对手异常 */
public class MatchUnavailableException extends BusinessException {
    public MatchUnavailableException(String message) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE);
    }
    public MatchUnavailableException() {
        this("当前无法匹配到对手");
    }
}
