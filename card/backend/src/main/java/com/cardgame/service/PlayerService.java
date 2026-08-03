package com.cardgame.service;

import com.cardgame.model.dto.response.LeaderboardResponse;
import com.cardgame.model.entity.User;

import java.util.List;

/** 玩家数据管理服务 */
public interface PlayerService {
    User getPlayerInfo(Long userId);
    void addGold(Long userId, int amount);
    void deductGold(Long userId, int amount);
    void addPoints(Long userId, int points);
    List<LeaderboardResponse> getLeaderboard(int top);
}
