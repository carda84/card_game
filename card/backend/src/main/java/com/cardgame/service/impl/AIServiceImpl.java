package com.cardgame.service.impl;

import com.cardgame.service.AIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AIServiceImpl implements AIService {

    @Override
    public void executeAITurn(Long sessionId) {
        // TODO: AI 决策逻辑
        log.info("AI 执行回合，sessionId={}", sessionId);
    }
}
