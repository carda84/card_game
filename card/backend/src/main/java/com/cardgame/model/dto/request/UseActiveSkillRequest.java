package com.cardgame.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UseActiveSkillRequest {
    @NotNull private Long sessionId;
}
