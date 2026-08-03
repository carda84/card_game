package com.cardgame.service;

import com.cardgame.model.entity.Card;

import java.util.List;

/** 印记效果解析与攻击结算服务 */
public interface CardLogicService {
    int calculateDamage(Card attacker, Card defender);
    List<Long> resolveSigilEffects(Long sessionId, Card card, String phase);
    boolean canAttack(Card card);
}
