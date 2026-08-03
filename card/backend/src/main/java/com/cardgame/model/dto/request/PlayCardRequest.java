package com.cardgame.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PlayCardRequest {
    @NotNull private Long sessionId;
    @NotNull private Integer handCardIndex;
    @NotNull private Integer slotIndex; // 己方空位 0-3
    /** 血献祭时选中的牌桌格位索引列表（0-3） */
    private List<Integer> sacrificeSlotIndices;
}
