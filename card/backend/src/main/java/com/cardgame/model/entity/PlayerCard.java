package com.cardgame.model.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 玩家拥有的卡牌
 * 记录每个玩家持有的卡牌及数量
 */
@Entity
@Table(name = "player_cards", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "card_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "card_id", nullable = false)
    private Long cardId;

    /** 持有数量 */
    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 0;
}
