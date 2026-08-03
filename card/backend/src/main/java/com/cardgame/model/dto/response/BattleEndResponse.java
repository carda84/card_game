package com.cardgame.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BattleEndResponse {
    private String result;     // WIN / LOSE / SURRENDER
    private Integer goldReward;
    private Integer pointsChange;
    private Integer turns;
    private BoardStateResponse finalBoardState;
}
