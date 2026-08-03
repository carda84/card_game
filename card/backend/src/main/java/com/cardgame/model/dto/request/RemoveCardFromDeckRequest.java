package com.cardgame.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RemoveCardFromDeckRequest {
    @NotNull private Long deckId;
    @NotNull private Long cardId;
}
