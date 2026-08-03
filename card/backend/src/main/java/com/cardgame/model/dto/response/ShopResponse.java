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
public class ShopResponse {
    private List<ShopItemDto> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShopItemDto {
        private Long id;
        private Long cardId;
        private String cardName;
        private Integer price;
        private Integer stock;
        private Boolean owned;
        private CardDetailResponse card;
    }
}
