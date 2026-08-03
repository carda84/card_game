package com.cardgame.service;

import com.cardgame.model.dto.response.CardStatResponse;

import java.util.List;

/** 卡牌统计服务 */
public interface StatisticsService {
    List<CardStatResponse> getCardStats(Long userId);
    List<CardStatResponse> getGlobalCardStats();
}
