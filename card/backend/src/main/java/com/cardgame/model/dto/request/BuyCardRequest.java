package com.cardgame.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class BuyCardRequest {
    @NotNull(message = "cardId不能为空")
    private Long cardId;
}
