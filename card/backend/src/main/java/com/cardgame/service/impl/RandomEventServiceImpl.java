package com.cardgame.service.impl;

import com.cardgame.service.RandomEventService;
import org.springframework.stereotype.Service;

@Service
public class RandomEventServiceImpl implements RandomEventService {

    @Override
    public String triggerRandomEvent(Long sessionId) {
        // TODO: 随机事件系统
        return "暂无随机事件";
    }
}
