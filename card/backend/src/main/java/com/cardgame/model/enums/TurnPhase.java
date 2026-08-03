package com.cardgame.model.enums;

/**
 * 回合阶段
 */
public enum TurnPhase {
    DRAW,          // 抽牌
    SELECT_CARD,   // 选牌
    SACRIFICE,     // 献祭
    PLAY_CARD,     // 出牌
    END_TURN,      // 结束回合
    AUTO_ATTACK    // 自动攻击结算
}
