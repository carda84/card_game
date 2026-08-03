package com.cardgame.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddCardToDeckRequest {
    @NotNull private Long deckId;
    @NotNull private Long cardId;
}
