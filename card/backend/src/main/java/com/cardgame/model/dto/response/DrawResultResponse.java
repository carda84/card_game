package com.cardgame.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrawResultResponse {
    private CardDetailResponse card; // 抽到的卡牌
    private String drawType;         // SQUIRREL / DECK
    private Integer remainingDeckSize;
}
