package com.cardgame.service;

import com.cardgame.model.dto.response.MatchResultResponse;

/** PvP 匹配服务 */
public interface MatchService {
    void joinQueue(Long userId, Long deckId);
    void leaveQueue(Long userId);
    MatchResultResponse findMatch(Long userId);
}
