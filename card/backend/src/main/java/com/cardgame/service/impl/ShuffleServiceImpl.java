package com.cardgame.service.impl;

import com.cardgame.dao.CardDao;
import com.cardgame.service.ShuffleService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShuffleServiceImpl implements ShuffleService {

    private final CardDao cardDao;

    public ShuffleServiceImpl(CardDao cardDao) {
        this.cardDao = cardDao;
    }

    @Override
    public List<Long> shuffleDeck(Long sessionId, List<Long> cardIds) {
        List<Long> filtered = filterPermanentDeadCards(cardIds);
        Collections.shuffle(filtered);
        return filtered;
    }

    @Override
    public boolean shouldReshuffle(Long sessionId) {
        // TODO: 判断是否需要重新洗牌
        return false;
    }

    @Override
    public List<Long> filterPermanentDeadCards(List<Long> cardIds) {
        // 排除 canShuffle=false 的卡牌（松鼠、蜜蜂、兔子等永久死亡卡）
        return cardIds.stream()
                .filter(id -> cardDao.findById(id).map(c -> Boolean.TRUE.equals(c.getCanShuffle())).orElse(false))
                .collect(Collectors.toList());
    }
}
