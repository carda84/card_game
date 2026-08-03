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
public class TurnEndResponse {
    private List<AttackResult> attacks; // 攻击结算列表
    private Integer playerHp;
    private Integer opponentHp;
    private Boolean isGameOver;
    private String winner; // null 表示未结束

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttackResult {
        private Integer attackerSlot;
        private Integer defenderSlot;
        private Integer damage;
        private Boolean defenderDied;
        private Boolean attackerDied; // 反击等情况
    }
}
