package com.cardgame.model.enums;

/**
 * 卡牌印记枚举（共 35 种）
 * 参考 card_design.txt 中的印记说明
 *
 * 印记是卡牌的特殊属性，可以同时存在多个。
 * 部分印记为卡牌固有属性，也可通过对局中的特殊事件赋予。
 * 所有印记只在打出后生效，打出未完成时禁止触发任何效果。
 */
public enum Sigil {

    // ===== 攻击相关 =====
    AIR_RAID("空袭", "跳过对面卡牌，直接攻击人物"),
    HIGH_JUMP("高跳", "使对面的卡牌空袭印记无效"),
    DOUBLE_STRIKE("双重打击", "攻击对面两次（算作两次攻击）"),
    SPLIT_STRIKE("分散打击", "攻击左侧和右侧各一次"),
    TRIPLE_STRIKE("三分打击", "攻击左侧、对面、右侧各一次"),
    POISON("剧毒", "直接杀死攻击对象，对玩家无效"),
    COUNTER("反击", "受到攻击时，攻击来源受到1伤害"),

    // ===== 成长/变化 =====
    HATCHLING("幼雏", "一回合结束后获得+1/+1并失去幼雏印记（部分卡牌例外）"),
    RANDOM_SIGIL("随机", "打出时获得一个随机印记"),

    // ===== 移动 =====
    MOVE("移动", "每回合结束移动一格，有阻挡则不移动"),
    PUSH("推动", "类似移动，但会推动相邻卡牌"),

    // ===== 生存/死亡 =====
    UNDYING("不死", "死亡后回到手牌，不进入弃牌堆"),
    EVERLASTING("生生不息", "被献祭后不会死亡"),
    BONE_KING("骨王", "死亡时获得4个骨头"),

    // ===== 献祭 =====
    PREMIUM_SACRIFICE("优质祭品", "献祭时算作3点血献祭"),
    INHERIT("继承", "被献祭时将自身攻击力/血量添加至打出的卡牌"),

    // ===== 生成卡牌 =====
    BEE("蜜蜂", "每次受到攻击时，己方获得一张蜜蜂"),
    RABBIT("兔子", "打出时将一张兔子添加至手牌"),
    ANT_QUEEN("蚁后", "打出时将一张蚂蚁添加至手牌"),
    FERTILE("丰产", "打出时将一张相同的卡牌添加至手牌"),
    LAY_EGG("下蛋", "打出时若对面为空位，放置破碎的蛋"),
    DAM("堤坝", "将两侧空位各放置一张堤坝"),
    BELL("铃铛", "将两侧空位各放置一张铃铛"),

    // ===== 防御/保护 =====
    SHIELD("盾", "受到的伤害减少1"),
    WATER_RAID("水袭", "在敌方回合该卡牌无法被攻击"),
    DIG("挖掘者", "当一个空位即将受到攻击，抵挡并出现在该位置"),
    GUARDIAN("守护者", "敌方打出卡牌时若对面为空位则移动至该位置"),
    TAIL_WHIP("断尾", "受攻击时留下尾巴抵挡，自身随机移动"),
    OCCUPY("占位", "己方卡牌被敌方攻击死亡时，出现在死亡位置"),

    // ===== 辅助 =====
    BONE_DIG("掘骨", "己方回合结束后获得1骨头"),
    STINKY("臭臭", "对面卡牌攻击力减少1"),
    ELDER("长老", "两侧己方卡牌攻击力加1"),
    SCAVENGE("拾荒", "双方有卡牌死亡时获得1额外骨头"),
    SEARCH("搜寻", "将抽牌堆的任意一张牌移动至手牌"),
    ITEM("道具", "获得一个随机道具，上限2个");

    private final String displayName;
    private final String description;

    Sigil(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据中文名称查找枚举
     */
    public static Sigil fromDisplayName(String name) {
        for (Sigil sigil : values()) {
            if (sigil.displayName.equals(name)) {
                return sigil;
            }
        }
        throw new IllegalArgumentException("未知印记: " + name);
    }
}
