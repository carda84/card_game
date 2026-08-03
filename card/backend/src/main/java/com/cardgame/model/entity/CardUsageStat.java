package com.cardgame.model.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 卡牌使用率统计
 */
@Entity
@Table(name = "card_usage_stats", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "card_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardUsageStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "card_id", nullable = false)
    private Long cardId;

    /** 卡组中出现次数 */
    @Column(nullable = false)
    @Builder.Default
    private Integer deckAppearCount = 0;

    /** 卡组总数（基数） */
    @Column(nullable = false)
    @Builder.Default
    private Integer deckTotalCount = 0;

    /** PvP 胜利次数 */
    @Column(nullable = false)
    @Builder.Default
    private Integer pvpWinCount = 0;

    /** PvP 总场次 */
    @Column(nullable = false)
    @Builder.Default
    private Integer pvpTotalCount = 0;
}
