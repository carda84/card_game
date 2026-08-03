package com.cardgame.service;

import java.util.List;

/** 洗牌逻辑服务 */
public interface ShuffleService {
    List<Long> shuffleDeck(Long sessionId, List<Long> cardIds);
    boolean shouldReshuffle(Long sessionId);
    List<Long> filterPermanentDeadCards(List<Long> cardIds);
}
