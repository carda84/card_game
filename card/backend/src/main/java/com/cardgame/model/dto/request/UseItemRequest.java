package com.cardgame.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UseItemRequest {
    @NotNull private Long sessionId;
    @NotNull private Integer itemIndex; // 道具 0 或 1
}
