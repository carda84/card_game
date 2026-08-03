package com.cardgame.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchResultResponse {
    private Boolean matched;
    private Long sessionId;
    private String opponentNickname;
    private String opponentUniqueTag;
    private Long opponentCharacterId;
    private String opponentCharacterName;
}
