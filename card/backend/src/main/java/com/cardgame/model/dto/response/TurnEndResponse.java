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
    /** PvE: AI 回合行动日志 */
    private List<AiAction> aiActions;

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

    /** AI 单步行动记录 */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AiAction {
        private String type;    // DRAW | PLAY_CARD | SACRIFICE | USE_ITEM | ATTACK
        private String detail;  // 人可读的描述
    }
}
