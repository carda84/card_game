package com.cardgame.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 商店商品
 * 每张卡牌在商店中的售卖信息
 */
@Entity
@Table(name = "shop_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_id", nullable = false)
    private Long cardId;

    /** 售价（金币） */
    @Column(nullable = false)
    private Integer price;

    /** 库存数量，-1 表示无限 */
    @Column(nullable = false)
    @Builder.Default
    private Integer stock = -1;

    /** 下次刷新时间 */
    @Column
    private LocalDateTime refreshTime;
}
