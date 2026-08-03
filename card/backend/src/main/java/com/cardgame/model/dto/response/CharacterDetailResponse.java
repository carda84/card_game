package com.cardgame.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 人物详情响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterDetailResponse {

    private Long id;
    private String name;
    private Integer maxHp;
    private Integer deckSize;
    private String specialAbilityDesc;
    private String initialItems;
    private List<String> initialItemList;
    private Boolean isDefault;
    private Integer price;
    private String imageUrl;
}
