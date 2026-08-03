package com.cardgame.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateDeckRequest {
    @NotBlank(message = "卡组名称不能为空")
    private String name;
    @NotNull(message = "人物ID不能为空")
    private Long characterId;
}
