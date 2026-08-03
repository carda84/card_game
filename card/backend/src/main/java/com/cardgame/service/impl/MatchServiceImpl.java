package com.cardgame.service.impl;

import com.cardgame.dao.*;
import com.cardgame.exception.BusinessException;
import com.cardgame.model.entity.Card;
import com.cardgame.model.entity.Deck;
import com.cardgame.model.entity.DeckCard;
import com.cardgame.model.entity.GameSession;
import com.cardgame.model.entity.User;
import com.cardgame.model.enums.*;
import com.cardgame.model.dto.response.MatchResultResponse;
import com.cardgame.service.MatchService;
import com.cardgame.service.ShuffleService;
import com.cardgame.util.GameConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MatchServiceImpl implements MatchService {

    /** 匹配队列：userId -> deckId */
    private final Map<Long, Long> matchQueue = new ConcurrentHashMap<>();

    private final UserDao userDao;
    private final DeckDao deckDao;
    private final DeckCardDao deckCardDao;
    private final CharacterDao characterDao;
    private final GameSessionDao gameSessionDao;
    private final CardDao cardDao;
    private final ShuffleService shuffleService;
    private final ObjectMapper objectMapper;

    public MatchServiceImpl(UserDao userDao, DeckDao deckDao, DeckCardDao deckCardDao,
                            CharacterDao characterDao, GameSessionDao gameSessionDao,
                            CardDao cardDao, ShuffleService shuffleService, ObjectMapper objectMapper) {
        this.userDao = userDao;
        this.deckDao = deckDao;
        this.deckCardDao = deckCardDao;
        this.characterDao = characterDao;
        this.gameSessionDao = gameSessionDao;
        this.cardDao = cardDao;
        this.shuffleService = shuffleService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void joinQueue(Long userId, Long deckId) {
        matchQueue.put(userId, deckId);
        log.info("用户 {} 加入匹配队列, deckId={}", userId, deckId);
    }

    @Override
    public void leaveQueue(Long userId) {
        matchQueue.remove(userId);
        log.info("用户 {} 离开匹配队列", userId);
    }

    @Override
    @Transactional
    public MatchResultResponse findMatch(Long userId) {
        // 找队列中的其他玩家
        for (Map.Entry<Long, Long> entry : matchQueue.entrySet()) {
            Long opponentId = entry.getKey();
            if (opponentId.equals(userId)) continue;

            Long opponentDeckId = entry.getValue();
            Long myDeckId = matchQueue.get(userId);

            // 匹配成功！移除双方
            matchQueue.remove(userId);
            matchQueue.remove(opponentId);

            // 创建 PvP 对战会话
            GameSession session = createPvpSession(userId, myDeckId, opponentId, opponentDeckId);

            User opponent = userDao.findById(opponentId).orElse(null);
            com.cardgame.model.entity.Character oppChar = characterDao.findById(session.getOpponentCharacterId()).orElse(null);

            return MatchResultResponse.builder()
                    .matched(true)
                    .sessionId(session.getId())
                    .opponentNickname(opponent != null ? opponent.getNickname() : "未知")
                    .opponentUniqueTag(opponent != null ? opponent.getUniqueTag() : "000000")
                    .opponentCharacterId(session.getOpponentCharacterId())
                    .opponentCharacterName(oppChar != null ? oppChar.getName() : "未知")
                    .build();
        }

        return MatchResultResponse.builder().matched(false).build();
    }

    private GameSession createPvpSession(Long player1Id, Long deck1Id, Long player2Id, Long deck2Id) {
        Deck deck1 = deckDao.findById(deck1Id).orElseThrow(() -> new BusinessException("卡组不存在"));
        Deck deck2 = deckDao.findById(deck2Id).orElseThrow(() -> new BusinessException("对手卡组不存在"));

        com.cardgame.model.entity.Character char1 = characterDao.findById(deck1.getCharacterId()).orElseThrow(() -> new BusinessException("人物不存在"));
        com.cardgame.model.entity.Character char2 = characterDao.findById(deck2.getCharacterId()).orElseThrow(() -> new BusinessException("对手人物不存在"));

        boolean isP1First = new Random().nextBoolean();

        // 洗牌和抽牌（玩家1）
        List<Long> p1CardIds = deckCardDao.findByDeckId(deck1.getId()).stream()
                .map(DeckCard::getCardId).collect(Collectors.toList());
        List<Long> p1Shuffled = shuffleService.shuffleDeck(null, p1CardIds);
        List<Long> p1Hand = new ArrayList<>(p1Shuffled.subList(0, Math.min(5, p1Shuffled.size())));
        List<Long> p1DrawPile = new ArrayList<>(p1Shuffled.subList(Math.min(5, p1Shuffled.size()), p1Shuffled.size()));

        // 洗牌和抽牌（玩家2）
        List<Long> p2CardIds = deckCardDao.findByDeckId(deck2.getId()).stream()
                .map(DeckCard::getCardId).collect(Collectors.toList());
        List<Long> p2Shuffled = shuffleService.shuffleDeck(null, p2CardIds);
        List<Long> p2Hand = new ArrayList<>(p2Shuffled.subList(0, Math.min(5, p2Shuffled.size())));
        List<Long> p2DrawPile = new ArrayList<>(p2Shuffled.subList(Math.min(5, p2Shuffled.size()), p2Shuffled.size()));

        try {
            BoardData board = new BoardData(
                    Arrays.asList(null, null, null, null),
                    Arrays.asList(null, null, null, null)
            );
            String boardJson = objectMapper.writeValueAsString(board);

            GameSession session = GameSession.builder()
                    .mode(BattleMode.PVP)
                    .playerUserId(player1Id)
                    .opponentUserId(player2Id)
                    .playerCharacterId(char1.getId())
                    .opponentCharacterId(char2.getId())
                    .currentPlayer(isP1First ? player1Id : player2Id)
                    .turnNumber(0)
                    .cardsPlayedThisTurn(0)
                    .playerHp(char1.getMaxHp())
                    .opponentHp(char2.getMaxHp())
                    .playerMaxHp(char1.getMaxHp())
                    .opponentMaxHp(char2.getMaxHp())
                    .playerBones(0)
                    .opponentBones(0)
                    .playerBloodThisTurn(0)
                    .turnPhase(TurnPhase.DRAW)
                    .sessionStatus(SessionStatus.IN_PROGRESS)
                    .boardState(boardJson)
                    .playerHand(objectMapper.writeValueAsString(p1Hand))
                    .playerDrawPile(objectMapper.writeValueAsString(p1DrawPile))
                    .opponentHand(objectMapper.writeValueAsString(p2Hand))
                    .opponentDrawPile(objectMapper.writeValueAsString(p2DrawPile))
                    .playerItems(objectMapper.writeValueAsString(char1.getInitialItemList().stream()
                            .map(ItemType::getDisplayName).collect(Collectors.toList())))
                    .opponentItems(objectMapper.writeValueAsString(char2.getInitialItemList().stream()
                            .map(ItemType::getDisplayName).collect(Collectors.toList())))
                    .build();

            gameSessionDao.save(session);
            return session;
        } catch (Exception e) {
            throw new BusinessException("创建对战失败: " + e.getMessage());
        }
    }

    /** 棋盘数据（与 BattleServiceImpl 相同结构） */
    static class SlotData {
        public Long cardId;
        public Integer currentHealth;
        public Integer attack;
        public String sigils;
        public Boolean isSpecialAttack;

        public SlotData() {}
        public SlotData(Long cardId, Integer currentHealth, Integer attack, String sigils, Boolean isSpecialAttack) {
            this.cardId = cardId;
            this.currentHealth = currentHealth;
            this.attack = attack;
            this.sigils = sigils;
            this.isSpecialAttack = isSpecialAttack;
        }
    }

    static class BoardData {
        public List<SlotData> playerSlots;
        public List<SlotData> opponentSlots;

        public BoardData() {}
        public BoardData(List<SlotData> playerSlots, List<SlotData> opponentSlots) {
            this.playerSlots = playerSlots;
            this.opponentSlots = opponentSlots;
        }
    }
}
