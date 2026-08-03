package com.cardgame.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeckDetailResponse {
    private Long id;
    private String name;
    private Long characterId;
    private String characterName;
    private List<CardDetailResponse> cards;
    private Integer cardCount;
    private Integer maxCardCount;
    private Integer legendaryCount;
    private Boolean isValid; // 卡组校验是否通过
    private String validationMessage;
}
