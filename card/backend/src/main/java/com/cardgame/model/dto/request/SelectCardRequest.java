package com.cardgame.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SelectCardRequest {
    @NotNull private Long sessionId;
    @NotNull private Integer handCardIndex;
}
