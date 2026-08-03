package com.cardgame.service.impl;

import com.cardgame.dao.CardDao;
import com.cardgame.dao.GameSessionDao;
import com.cardgame.exception.BusinessException;
import com.cardgame.model.entity.Card;
import com.cardgame.model.entity.GameSession;
import com.cardgame.service.SacrificeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SacrificeServiceImpl implements SacrificeService {

    private final GameSessionDao gameSessionDao;
    private final CardDao cardDao;

    public SacrificeServiceImpl(GameSessionDao gameSessionDao, CardDao cardDao) {
        this.gameSessionDao = gameSessionDao;
        this.cardDao = cardDao;
    }

    @Override
    public void validateBloodSacrifice(Long sessionId, List<Long> sacrificeCardIds, int requiredBlood) {
        if (sacrificeCardIds.size() != requiredBlood) {
            throw new BusinessException("血献祭数量不匹配，需要 " + requiredBlood + " 张卡牌");
        }
        for (Long cardId : sacrificeCardIds) {
            Card card = cardDao.findById(cardId)
                    .orElseThrow(() -> new BusinessException("献祭卡牌不存在"));
            if (!card.getCanSacrifice()) {
                throw new BusinessException(card.getName() + " 不可被献祭");
            }
        }
    }

    @Override
    public void validateBoneSacrifice(Long sessionId, int requiredBones) {
        GameSession session = gameSessionDao.findById(sessionId)
                .orElseThrow(() -> new BusinessException("对战不存在"));
        if (session.getPlayerBones() < requiredBones) {
            throw new BusinessException("骨头不足，需要 " + requiredBones + " 个，当前拥有 " + session.getPlayerBones() + " 个");
        }
    }
}
