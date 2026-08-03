package com.cardgame.model.dto.request;

import com.cardgame.model.enums.BattleMode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StartBattleRequest {
    @NotNull private Long deckId;
    @NotNull private BattleMode mode;
    /** PvE 模式下的关卡 ID */
    private Long levelId;
}
