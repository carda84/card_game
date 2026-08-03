package com.cardgame.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardStatResponse {
    private Long cardId;
    private String cardName;
    private Double deckAppearRate;  // 卡组出现占比（百分比，两位小数）
    private Double pvpWinRate;     // PvP 胜率（百分比，两位小数）
    private Integer totalGames;
}
