package com.cardgame.service;

/** 回合流程服务 */
public interface TurnService {
    void startNewTurn(Long sessionId);
    void proceedToPhase(Long sessionId, String phase);
    boolean isPlayerTurn(Long sessionId, Long userId);
}
