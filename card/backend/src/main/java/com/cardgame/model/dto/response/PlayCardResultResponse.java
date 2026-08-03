package com.cardgame.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayCardResultResponse {
    private Boolean success;
    private Integer slotIndex;
    private CardDetailResponse playedCard;
    private List<Long> sacrificedCardIds; // 被献祭的卡牌
    private BoardStateResponse boardState;
}
