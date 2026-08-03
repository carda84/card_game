package com.cardgame.service;

import com.cardgame.model.dto.response.BattleEndResponse;

/** PvP 对战逻辑服务 */
public interface PvpBattleService {
    void handleTurnTimeout(Long sessionId, Long userId);
    BattleEndResponse forceEndByTimeout(Long sessionId);
}
