package com.cardgame.service.impl;

import com.cardgame.model.dto.response.BattleEndResponse;
import com.cardgame.service.PvpBattleService;
import org.springframework.stereotype.Service;

@Service
public class PvpBattleServiceImpl implements PvpBattleService {

    @Override
    public void handleTurnTimeout(Long sessionId, Long userId) {
        // TODO: 处理回合超时
    }

    @Override
    public BattleEndResponse forceEndByTimeout(Long sessionId) {
        // TODO: 强制结束对战
        return BattleEndResponse.builder().result("TIMEOUT").build();
    }
}
