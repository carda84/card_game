package com.cardgame.exception;

import org.springframework.http.HttpStatus;

/** 非法战斗操作异常 */
public class InvalidBattleActionException extends BusinessException {
    public InvalidBattleActionException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
