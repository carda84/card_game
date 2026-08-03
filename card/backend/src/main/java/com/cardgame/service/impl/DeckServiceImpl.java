package com.cardgame.service.impl;

import com.cardgame.dao.*;
import com.cardgame.exception.BusinessException;
import com.cardgame.model.dto.request.CreateDeckRequest;
import com.cardgame.model.dto.response.CardDetailResponse;
import com.cardgame.model.dto.response.DeckDetailResponse;
import com.cardgame.model.entity.Card;
import com.cardgame.model.entity.Character;
import com.cardgame.model.entity.Deck;
import com.cardgame.model.entity.DeckCard;
import com.cardgame.model.entity.PlayerCard;
import com.cardgame.service.DeckService;
import com.cardgame.util.GameConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DeckServiceImpl implements DeckService {

    /** 用户最大卡组数量 */
    private static final int MAX_DECKS_PER_USER = 20;

    private final DeckDao deckDao;
    private final DeckCardDao deckCardDao;
    private final CardDao cardDao;
    private final CharacterDao characterDao;
    private final PlayerCardDao playerCardDao;

    public DeckServiceImpl(DeckDao deckDao, DeckCardDao deckCardDao,
                           CardDao cardDao, CharacterDao characterDao,
                           PlayerCardDao playerCardDao) {
        this.deckDao = deckDao;
        this.deckCardDao = deckCardDao;
        this.cardDao = cardDao;
        this.characterDao = characterDao;
        this.playerCardDao = playerCardDao;
    }

    @Override
    @Transactional
    public DeckDetailResponse createDeck(Long userId, CreateDeckRequest request) {
        // 校验卡组数量上限
        long currentCount = deckDao.countByUserId(userId);
        if (currentCount >= MAX_DECKS_PER_USER) {
            throw new BusinessException("卡组数量已达上限（最多 " + MAX_DECKS_PER_USER + " 个）");
        }

        Character character = characterDao.findById(request.getCharacterId())
                .orElseThrow(() -> new BusinessException("人物不存在", HttpStatus.NOT_FOUND));

        Deck deck = Deck.builder()
                .userId(userId)
                .characterId(character.getId())
                .name(request.getName())
                .build();
        deckDao.save(deck);
        return getDeckDetail(userId, deck.getId());
    }

    @Override
    public DeckDetailResponse getDeckDetail(Long userId, Long deckId) {
        Deck deck = deckDao.findById(deckId)
                .orElseThrow(() -> new BusinessException("卡组不存在", HttpStatus.NOT_FOUND));

        List<DeckCard> deckCards = deckCardDao.findByDeckId(deckId);
        List<CardDetailResponse> cards = deckCards.stream()
                .map(dc -> cardDao.findById(dc.getCardId()).orElse(null))
                .filter(c -> c != null)
                .map(this::toCardDetail)
                .collect(Collectors.toList());

        long legendaryCount = cards.stream().filter(c -> Boolean.TRUE.equals(c.getIsLegendary())).count();
        Character character = characterDao.findById(deck.getCharacterId()).orElse(null);
        int requiredSize = character != null ? character.getDeckSize() : 0;
        boolean isValid = cards.size() == requiredSize && legendaryCount <= GameConstants.MAX_LEGENDARY_IN_DECK;

        String validationMessage = null;
        if (!isValid) {
            if (cards.size() != requiredSize) {
                validationMessage = "卡组需要 " + requiredSize + " 张卡牌，当前 " + cards.size() + " 张";
            } else if (legendaryCount > GameConstants.MAX_LEGENDARY_IN_DECK) {
                validationMessage = "传奇卡牌不能超过 " + GameConstants.MAX_LEGENDARY_IN_DECK + " 张";
            }
        }

        return DeckDetailResponse.builder()
                .id(deck.getId())
                .name(deck.getName())
                .characterId(deck.getCharacterId())
                .characterName(character != null ? character.getName() : null)
                .cards(cards)
                .cardCount(cards.size())
                .maxCardCount(requiredSize)
                .legendaryCount((int) legendaryCount)
                .isValid(isValid)
                .validationMessage(validationMessage)
                .build();
    }

    @Override
    public List<DeckDetailResponse> getUserDecks(Long userId) {
        return deckDao.findByUserId(userId).stream()
                .map(d -> getDeckDetail(userId, d.getId()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addCardToDeck(Long userId, Long deckId, Long cardId) {
        Deck deck = deckDao.findById(deckId)
                .orElseThrow(() -> new BusinessException("卡组不存在"));
        if (!deck.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此卡组");
        }

        // 校验用户是否拥有该卡牌
        playerCardDao.findByUserIdAndCardId(userId, cardId)
                .orElseThrow(() -> new BusinessException("你尚未拥有此卡牌，请先在商店购买"));

        Card card = cardDao.findById(cardId)
                .orElseThrow(() -> new BusinessException("卡牌不存在"));

        // 校验卡组是否已满
        Character character = characterDao.findById(deck.getCharacterId()).orElse(null);
        int maxSize = character != null ? character.getDeckSize() : 0;
        long currentSize = deckCardDao.countByDeckId(deckId);
        if (currentSize >= maxSize) {
            throw new BusinessException("卡组已满（" + maxSize + " 张）");
        }

        // 校验该卡在卡组中的数量上限（maxDeckCount）
        List<DeckCard> existingCards = deckCardDao.findByDeckId(deckId);
        long sameCardCount = existingCards.stream()
                .filter(dc -> dc.getCardId().equals(cardId))
                .count();
        if (card.getMaxDeckCount() != null && card.getMaxDeckCount() > 0
                && sameCardCount >= card.getMaxDeckCount()) {
            throw new BusinessException("此卡牌在卡组中最多只能放 " + card.getMaxDeckCount() + " 张");
        }

        // 校验传奇卡上限
        if (Boolean.TRUE.equals(card.getIsLegendary())) {
            long legendaryCount = existingCards.stream()
                    .filter(dc -> {
                        Card c = cardDao.findById(dc.getCardId()).orElse(null);
                        return c != null && Boolean.TRUE.equals(c.getIsLegendary());
                    })
                    .count();
            if (legendaryCount >= GameConstants.MAX_LEGENDARY_IN_DECK) {
                throw new BusinessException("传奇卡牌不能超过 " + GameConstants.MAX_LEGENDARY_IN_DECK + " 张");
            }
        }

        DeckCard dc = DeckCard.builder().deckId(deckId).cardId(cardId).build();
        deckCardDao.save(dc);
    }

    @Override
    @Transactional
    public void removeCardFromDeck(Long userId, Long deckId, Long cardId) {
        Deck deck = deckDao.findById(deckId)
                .orElseThrow(() -> new BusinessException("卡组不存在"));
        if (!deck.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此卡组");
        }

        List<DeckCard> list = deckCardDao.findByDeckId(deckId);
        list.stream().filter(dc -> dc.getCardId().equals(cardId))
                .findFirst()
                .ifPresent(deckCardDao::delete);
    }

    @Override
    @Transactional
    public void deleteDeck(Long userId, Long deckId) {
        Deck deck = deckDao.findById(deckId)
                .orElseThrow(() -> new BusinessException("卡组不存在"));
        if (!deck.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此卡组");
        }
        deckCardDao.deleteByDeckId(deckId);
        deckDao.deleteById(deckId);
    }

    @Override
    public List<CardDetailResponse> getOwnedCards(Long userId) {
        // 获取用户已拥有的卡牌 ID 集合
        Set<Long> ownedCardIds = playerCardDao.findByUserId(userId).stream()
                .map(PlayerCard::getCardId)
                .collect(Collectors.toSet());

        // 返回已拥有卡牌的详细信息
        return cardDao.findAll().stream()
                .filter(c -> ownedCardIds.contains(c.getId()))
                .map(this::toCardDetail)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DeckDetailResponse renameDeck(Long userId, Long deckId, String newName) {
        Deck deck = deckDao.findById(deckId)
                .orElseThrow(() -> new BusinessException("卡组不存在"));
        if (!deck.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此卡组");
        }
        if (newName == null || newName.isBlank()) {
            throw new BusinessException("卡组名称不能为空");
        }
        if (newName.length() > 100) {
            throw new BusinessException("卡组名称不能超过100个字符");
        }
        deck.setName(newName);
        deckDao.save(deck);
        return getDeckDetail(userId, deckId);
    }

    private CardDetailResponse toCardDetail(Card c) {
        return CardDetailResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .attack(c.getAttack())
                .health(c.getHealth())
                .bloodCost(c.getBloodCost())
                .boneCost(c.getBoneCost())
                .sigils(c.getSigils())
                .races(c.getRaces())
                .maxDeckCount(c.getMaxDeckCount())
                .isLegendary(c.getIsLegendary())
                .description(c.getDescription())
                .build();
    }
}
