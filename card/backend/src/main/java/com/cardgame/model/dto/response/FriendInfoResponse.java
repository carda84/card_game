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
public class FriendInfoResponse {
    private Long userId;
    private String nickname;
    private String uniqueTag;
    private Integer points;
    private Integer gold;
    private List<BattleRecordResponse> recentRecords;
}
