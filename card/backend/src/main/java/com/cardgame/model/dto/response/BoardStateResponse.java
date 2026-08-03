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
public class BoardStateResponse {
    private Integer turnNumber;
    private String turnPhase;
    private List<SlotInfo> playerSlots;   // 玩家 4 格位
    private List<SlotInfo> opponentSlots; // 对手 4 格位
    private List<CardDetailResponse> playerHand;
    private Integer opponentHandCount;
    private Integer playerBones;
    private Integer opponentBones;
    private Integer playerHp;
    private Integer opponentHp;
    private List<String> playerItems;
    private List<String> opponentItems;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SlotInfo {
        private Integer index;
        private CardDetailResponse card; // null 表示空位
        private Boolean isEmpty;
    }
}
