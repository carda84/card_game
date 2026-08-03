package com.cardgame.model.dto.request;

import com.cardgame.model.enums.DrawType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DrawCardRequest {
    @NotNull private Long sessionId;
    @NotNull private DrawType drawType;
}
