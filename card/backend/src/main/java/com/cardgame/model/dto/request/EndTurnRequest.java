package com.cardgame.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EndTurnRequest {
    @NotNull private Long sessionId;
}
