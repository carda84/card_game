package com.cardgame.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应
 * 返回 JWT Token 和用户基本信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /** JWT Token */
    private String token;

    /** 用户昵称 */
    private String nickname;

    /** 用户唯一标识（#138992） */
    private String uniqueTag;

    /** 金币余额 */
    private Integer gold;

    /** 积分 */
    private Integer points;

    /** 用户完整标识（昵称#138992） */
    public String getFullId() {
        return nickname + "#" + uniqueTag;
    }
}
