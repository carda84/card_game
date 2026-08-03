package com.cardgame.service.impl;

import com.cardgame.dao.CardDao;
import com.cardgame.dao.CardUsageStatDao;
import com.cardgame.model.dto.response.CardStatResponse;
import com.cardgame.model.entity.Card;
import com.cardgame.model.entity.CardUsageStat;
import com.cardgame.service.StatisticsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final CardUsageStatDao statDao;
    private final CardDao cardDao;

    public StatisticsServiceImpl(CardUsageStatDao statDao, CardDao cardDao) {
        this.statDao = statDao;
        this.cardDao = cardDao;
    }

    @Override
    public List<CardStatResponse> getCardStats(Long userId) {
        return statDao.findByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CardStatResponse> getGlobalCardStats() {
        // TODO: 全局统计
        return List.of();
    }

    private CardStatResponse toResponse(CardUsageStat stat) {
        Card card = cardDao.findById(stat.getCardId()).orElse(null);
        double appearRate = stat.getDeckTotalCount() > 0
                ? Math.round(stat.getDeckAppearCount() * 10000.0 / stat.getDeckTotalCount()) / 100.0
                : 0.0;
        double winRate = stat.getPvpTotalCount() > 0
                ? Math.round(stat.getPvpWinCount() * 10000.0 / stat.getPvpTotalCount()) / 100.0
                : 0.0;
        return CardStatResponse.builder()
                .cardId(stat.getCardId())
                .cardName(card != null ? card.getName() : "未知")
                .deckAppearRate(appearRate)
                .pvpWinRate(winRate)
                .totalGames(stat.getPvpTotalCount())
                .build();
    }
}
