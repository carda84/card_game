package com.cardgame.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 用户实体
 * 展示格式：昵称#唯一标识，如 张三#138992
 * uniqueTag 为 6 位随机数字，全局唯一，是标识用户的凭证
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 昵称，用户自由取名，可重复 */
    @Column(nullable = false, length = 50)
    private String nickname;

    /** 唯一标识，6 位随机数字（如 138992），全局唯一 */
    @Column(nullable = false, unique = true, length = 6)
    private String uniqueTag;

    /** 注册邮箱，全局唯一 */
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    /** 密码哈希（BCrypt） */
    @Column(nullable = false, length = 255)
    private String passwordHash;

    /** 金币，用于商店购买卡牌 */
    @Column(nullable = false)
    @Builder.Default
    private Integer gold = 100;

    /** 积分，用于排名展示 */
    @Column(nullable = false)
    @Builder.Default
    private Integer points = 0;

    /**
     * 头像文件名（如 "avatar_1234567890_abc.jpg"）
     * 上传头像后保存，前端通过静态资源路径访问
     */
    @Column(length = 100)
    private String avatar;

    /**
     * 已解锁人物 ID 列表（逗号分隔，如 "1,3"）
     * 默认人物（isDefault=true）无需解锁，始终可用
     * 购买解锁的人物 ID 会追加到此字段
     */
    @Column(length = 200)
    private String unlockedCharacters;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 返回用户完整标识，格式：昵称#唯一标识
     */
    public String getFullId() {
        return nickname + "#" + uniqueTag;
    }
}
