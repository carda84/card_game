package com.cardgame.service;

import com.cardgame.model.dto.request.CreateDeckRequest;
import com.cardgame.model.dto.response.CardDetailResponse;
import com.cardgame.model.dto.response.DeckDetailResponse;

import java.util.List;

/** 卡组构建与校验服务 */
public interface DeckService {
    DeckDetailResponse createDeck(Long userId, CreateDeckRequest request);
    DeckDetailResponse getDeckDetail(Long userId, Long deckId);
    List<DeckDetailResponse> getUserDecks(Long userId);
    void addCardToDeck(Long userId, Long deckId, Long cardId);
    void removeCardFromDeck(Long userId, Long deckId, Long cardId);
    void deleteDeck(Long userId, Long deckId);

    /** 获取用户已拥有的卡牌列表（用于组卡） */
    List<CardDetailResponse> getOwnedCards(Long userId);

    /** 重命名卡组 */
    DeckDetailResponse renameDeck(Long userId, Long deckId, String newName);
}
