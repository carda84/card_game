package com.cardgame.model.enums;

/**
 * 道具枚举
 * 所有道具仅限单次使用，玩家最多同时拥有 2 个道具
 *
 * 道具获取途径：
 *   - 人物初始道具（如卡德的画笔）
 *   - 卡牌印记"道具"触发时随机获得
 *   - 商店购买（未来扩展）
 */
public enum ItemType {

    PAINTBRUSH("画笔", "抹去对手牌桌上所有卡牌的印记"),
    SCISSORS("剪刀", "摧毁对手牌桌上任意一张卡牌"),
    FISHHOOK("鱼钩", "若对手牌桌上某张牌有对位空着，将其拉至己方牌桌，变为己方卡牌"),
    FAN("扇子", "本回合所有己方卡牌获得空袭印记");

    private final String displayName;
    private final String description;

    ItemType(String displayName, String description) {
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
    public static ItemType fromDisplayName(String name) {
        for (ItemType item : values()) {
            if (item.displayName.equals(name)) {
                return item;
            }
        }
        throw new IllegalArgumentException("未知道具: " + name);
    }
}
