package com.cardgame.util;

/**
 * 游戏全局常量
 * 所有游戏规则数值在此集中管理
 */
public final class GameConstants {

    private GameConstants() {}

    // ===== 棋盘 =====
    /** 双方各有的格位数 */
    public static final int BOARD_SLOTS_PER_SIDE = 4;

    // ===== 手牌与抽牌 =====
    /** 初始手牌数量 */
    public static final int INITIAL_HAND_SIZE = 5;

    // ===== 卡组 =====
    /** 传奇卡在牌组中的最大数量 */
    public static final int MAX_LEGENDARY_IN_DECK = 3;

    // ===== 道具 =====
    /** 玩家同时拥有的道具上限 */
    public static final int MAX_ITEMS = 2;

    // ===== PvP =====
    /** PvP 每回合时间限制（秒） */
    public static final int PVP_TURN_TIMEOUT_SECONDS = 30;

    // ===== 记录 =====
    /** 保存每个用户最近的战斗记录数 */
    public static final int MAX_BATTLE_RECORDS = 20;

    // ===== 初始资源 =====
    /** 新用户初始金币 */
    public static final int INITIAL_GOLD = 100;

    /** 新用户初始积分 */
    public static final int INITIAL_POINTS = 0;

    // ===== 人物属性范围 =====
    /** 人物血量最小值 */
    public static final int MIN_CHARACTER_HP = 20;
    /** 人物血量最大值 */
    public static final int MAX_CHARACTER_HP = 40;
    /** 人物卡组数量最小值 */
    public static final int MIN_DECK_SIZE = 10;
    /** 人物卡组数量最大值 */
    public static final int MAX_DECK_SIZE = 30;

    // ===== 用户标识 =====
    /** 用户唯一标识位数 */
    public static final int UNIQUE_TAG_LENGTH = 6;
}