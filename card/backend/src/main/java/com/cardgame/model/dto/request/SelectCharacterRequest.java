package com.cardgame.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SelectCharacterRequest {
    @NotNull private Long characterId;
}
