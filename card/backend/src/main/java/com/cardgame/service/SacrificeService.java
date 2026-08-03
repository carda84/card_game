package com.cardgame.service;

/** 献祭规则判定服务 */
public interface SacrificeService {
    void validateBloodSacrifice(Long sessionId, java.util.List<Long> sacrificeCardIds, int requiredBlood);
    void validateBoneSacrifice(Long sessionId, int requiredBones);
}
