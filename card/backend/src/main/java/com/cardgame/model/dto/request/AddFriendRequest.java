package com.cardgame.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddFriendRequest {
    @NotBlank(message = "目标用户标识不能为空")
    private String targetId; // 用户唯一标识 如 "昵称#138992"
}
