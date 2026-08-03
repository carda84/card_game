package com.cardgame.service.impl;

import com.cardgame.model.entity.Card;
import com.cardgame.service.CardLogicService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CardLogicServiceImpl implements CardLogicService {

    @Override
    public int calculateDamage(Card attacker, Card defender) {
        if (attacker.getAttack() == null) return 0; // 特殊攻击力另行处理
        return attacker.getAttack();
    }

    @Override
    public List<Long> resolveSigilEffects(Long sessionId, Card card, String phase) {
        // TODO: 解析印记效果
        return Collections.emptyList();
    }

    @Override
    public boolean canAttack(Card card) {
        return card.getAttack() != null && card.getAttack() > 0;
    }
}
