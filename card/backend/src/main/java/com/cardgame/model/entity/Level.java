package com.cardgame.model.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * PvE 关卡
 */
@Entity
@Table(name = "levels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Level {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 关卡名称 */
    @Column(nullable = false, length = 100)
    private String name;

    /** 关卡描述 */
    @Column(length = 500)
    private String description;

    /** 难度等级（1-10） */
    @Column(nullable = false)
    @Builder.Default
    private Integer difficulty = 1;

    /** 奖励倍率 */
    @Column(nullable = false)
    @Builder.Default
    private Double rewardMultiplier = 1.0;
}
