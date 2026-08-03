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
public class BattleStartResponse {
    private Long sessionId;
    private List<CardDetailResponse> initialHand; // 初始 5 张手牌
    private String opponentName;
    private Long opponentCharacterId;
    private String opponentCharacterName;
    private Boolean isPlayerFirst; // 是否先手
}
