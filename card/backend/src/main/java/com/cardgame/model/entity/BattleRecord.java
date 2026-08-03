package com.cardgame.model.entity;

import com.cardgame.model.enums.BattleMode;
import com.cardgame.model.enums.BattleResult;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 对战记录
 * 保存每个用户最近的 20 场战斗
 */
@Entity
@Table(name = "battle_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BattleRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "opponent_id")
    private Long opponentId;

    /** 对战模式 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private BattleMode mode;

    /** 对战结果 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BattleResult result;

    @Column(name = "self_character_id")
    private Long selfCharacterId;

    @Column(name = "opponent_character_id")
    private Long opponentCharacterId;

    /** 己方卡组快照（JSON） */
    @Column(name = "self_deck_snapshot", columnDefinition = "TEXT")
    private String selfDeckSnapshot;

    /** 对方卡组快照（JSON） */
    @Column(name = "opponent_deck_snapshot", columnDefinition = "TEXT")
    private String opponentDeckSnapshot;

    /** 回合数 */
    @Column
    private Integer turns;

    /** 奖励（金币/积分） */
    @Column
    private Integer reward;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
