package com.cardgame.model.entity;

import com.cardgame.model.enums.BattleMode;
import com.cardgame.model.enums.SessionStatus;
import com.cardgame.model.enums.TurnPhase;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 游戏会话
 * 存储一场战斗的完整运行时状态
 */
@Entity
@Table(name = "game_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 对战模式 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private BattleMode mode;

    /** PvE 关卡 ID（仅 PvE 模式使用） */
    @Column(name = "level_id")
    private Long levelId;

    /** 玩家用户 ID */
    @Column(name = "player_user_id", nullable = false)
    private Long playerUserId;

    /** 对手用户 ID（PvP 模式） */
    @Column(name = "opponent_user_id")
    private Long opponentUserId;

    /** 玩家人物 ID */
    @Column(name = "player_character_id")
    private Long playerCharacterId;

    /** 对手人物 ID */
    @Column(name = "opponent_character_id")
    private Long opponentCharacterId;

    /** 当前回合数 */
    @Column(nullable = false)
    @Builder.Default
    private Integer turnNumber = 0;

    /** 本回合已出牌数 */
    @Column(nullable = false)
    @Builder.Default
    private Integer cardsPlayedThisTurn = 0;

    /** 当前行动方用户 ID */
    @Column(name = "current_player_id")
    private Long currentPlayer;

    /** 棋盘状态（JSON：双方各 4 格位） */
    @Column(name = "board_state", columnDefinition = "TEXT")
    private String boardState;

    /** 玩家手牌（JSON 数组） */
    @Column(name = "player_hand", columnDefinition = "TEXT")
    private String playerHand;

    /** 对手手牌（JSON 数组） */
    @Column(name = "opponent_hand", columnDefinition = "TEXT")
    private String opponentHand;

    /** 玩家抽牌堆（JSON 数组） */
    @Column(name = "player_draw_pile", columnDefinition = "TEXT")
    private String playerDrawPile;

    /** 对手抽牌堆（JSON 数组） */
    @Column(name = "opponent_draw_pile", columnDefinition = "TEXT")
    private String opponentDrawPile;

    /** 玩家骨头数 */
    @Column(nullable = false)
    @Builder.Default
    private Integer playerBones = 0;

    /** 对手骨头数 */
    @Column(nullable = false)
    @Builder.Default
    private Integer opponentBones = 0;

    /** 玩家当前血量 */
    @Column(nullable = false)
    @Builder.Default
    private Integer playerHp = 30;

    /** 对手当前血量 */
    @Column(nullable = false)
    @Builder.Default
    private Integer opponentHp = 30;

    /** 玩家最大血量 */
    @Column(nullable = false)
    @Builder.Default
    private Integer playerMaxHp = 30;

    /** 对手最大血量 */
    @Column(nullable = false)
    @Builder.Default
    private Integer opponentMaxHp = 30;

    /** 本回合临时血 */
    @Column(nullable = false)
    @Builder.Default
    private Integer playerBloodThisTurn = 0;

    /** 玩家道具栏（JSON，最多 2 个） */
    @Column(name = "player_items", length = 500)
    private String playerItems;

    /** 对手道具栏 */
    @Column(name = "opponent_items", length = 500)
    private String opponentItems;

    /** 初始抽牌保底 1 费血献祭卡是否已触发 */
    @Column(nullable = false)
    @Builder.Default
    private Boolean guaranteedBloodCardDrawn = false;

    /** 回合计时器（PvP 模式） */
    @Column(name = "turn_timer")
    private Integer turnTimer;

    /** 当前回合阶段 */
    @Enumerated(EnumType.STRING)
    @Column(name = "turn_phase", length = 20)
    private TurnPhase turnPhase;

    /** 会话状态 */
    @Enumerated(EnumType.STRING)
    @Column(name = "session_status", nullable = false, length = 20)
    @Builder.Default
    private SessionStatus sessionStatus = SessionStatus.WAITING;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
