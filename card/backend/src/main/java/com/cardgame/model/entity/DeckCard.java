package com.cardgame.model.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 卡组内卡牌关联表
 */
@Entity
@Table(name = "deck_cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeckCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "deck_id", nullable = false)
    private Long deckId;

    @Column(name = "card_id", nullable = false)
    private Long cardId;
}
