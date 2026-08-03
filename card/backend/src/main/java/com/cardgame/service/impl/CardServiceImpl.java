package com.cardgame.service.impl;

import com.cardgame.dao.CardDao;
import com.cardgame.exception.BusinessException;
import com.cardgame.model.entity.Card;
import com.cardgame.service.CardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CardServiceImpl implements CardService {

    private final CardDao cardDao;

    public CardServiceImpl(CardDao cardDao) {
        this.cardDao = cardDao;
    }

    @Override
    public List<Card> findAll() {
        return cardDao.findAll();
    }

    @Override
    public Card findById(Long id) {
        return cardDao.findById(id)
                .orElseThrow(() -> new BusinessException("卡牌不存在，ID: " + id, HttpStatus.NOT_FOUND));
    }

    @Override
    public Card findByName(String name) {
        return cardDao.findByName(name)
                .orElseThrow(() -> new BusinessException("卡牌不存在: " + name, HttpStatus.NOT_FOUND));
    }

    @Override
    public List<Card> findDeckableCards() {
        return cardDao.findByMaxDeckCountGreaterThan(0);
    }

    @Override
    public List<Card> findByRace(String race) {
        return cardDao.findByRace(race);
    }

    @Override
    public List<Card> findBySigil(String sigil) {
        return cardDao.findBySigil(sigil);
    }
}
