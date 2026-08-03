package com.cardgame.model.enums;

/**
 * 卡牌种族枚举
 * 种族是卡牌的属性之一，可能不唯一（如融合兽同时拥有多个种族）
 */
public enum Race {

    WOLF("狼"),
    REPTILE("爬行"),
    INSECT("虫"),
    DEER("鹿"),
    BIRD("鸟");

    private final String displayName;

    Race(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 根据中文名称查找枚举
     */
    public static Race fromDisplayName(String name) {
        for (Race race : values()) {
            if (race.displayName.equals(name)) {
                return race;
            }
        }
        throw new IllegalArgumentException("未知种族: " + name);
    }
}
