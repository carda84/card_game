package com.cardgame.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SacrificeRequest {
    @NotNull private Long sessionId;
    @NotNull private List<Long> sacrificeCardIds;
}
