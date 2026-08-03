package com.cardgame.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注册响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {

    private boolean success;
    private String message;

    /** 注册成功后返回用户的完整标识（昵称#ID） */
    private String fullId;

    public static RegisterResponse ok(String fullId) {
        return RegisterResponse.builder()
                .success(true)
                .message("注册成功")
                .fullId(fullId)
                .build();
    }

    public static RegisterResponse fail(String message) {
        return RegisterResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}
