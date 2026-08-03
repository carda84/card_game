package com.cardgame.service.impl;

import com.cardgame.dao.GameSessionDao;
import com.cardgame.exception.BusinessException;
import com.cardgame.model.entity.GameSession;
import com.cardgame.model.enums.TurnPhase;
import com.cardgame.service.TurnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TurnServiceImpl implements TurnService {

    private final GameSessionDao gameSessionDao;

    public TurnServiceImpl(GameSessionDao gameSessionDao) {
        this.gameSessionDao = gameSessionDao;
    }

    @Override
    public void startNewTurn(Long sessionId) {
        GameSession session = gameSessionDao.findById(sessionId)
                .orElseThrow(() -> new BusinessException("对战不存在"));
        session.setTurnNumber(session.getTurnNumber() + 1);
        session.setCardsPlayedThisTurn(0);
        session.setTurnPhase(TurnPhase.DRAW);
        gameSessionDao.save(session);
        log.info("新回合开始: sessionId={}, turn={}", sessionId, session.getTurnNumber());
    }

    @Override
    public void proceedToPhase(Long sessionId, String phase) {
        GameSession session = gameSessionDao.findById(sessionId)
                .orElseThrow(() -> new BusinessException("对战不存在"));
        TurnPhase newPhase = TurnPhase.valueOf(phase);
        session.setTurnPhase(newPhase);
        gameSessionDao.save(session);
    }

    @Override
    public boolean isPlayerTurn(Long sessionId, Long userId) {
        GameSession session = gameSessionDao.findById(sessionId)
                .orElseThrow(() -> new BusinessException("对战不存在"));
        if (session.getCurrentPlayer() == null) return true;
        return session.getCurrentPlayer().equals(userId);
    }
}
