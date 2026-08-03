package com.cardgame.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BattleRecordResponse {
    private Long id;
    private String mode;
    private String result;
    private String opponentName;
    private Long selfCharacterId;
    private Long opponentCharacterId;
    private Integer turns;
    private Integer reward;
    private LocalDateTime createdAt;
}
