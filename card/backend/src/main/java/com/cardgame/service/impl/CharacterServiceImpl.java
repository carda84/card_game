package com.cardgame.service.impl;

import com.cardgame.dao.CharacterDao;
import com.cardgame.exception.BusinessException;
import com.cardgame.model.entity.Character;
import com.cardgame.service.CharacterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CharacterServiceImpl implements CharacterService {

    private final CharacterDao characterDao;

    public CharacterServiceImpl(CharacterDao characterDao) {
        this.characterDao = characterDao;
    }

    @Override
    public List<Character> findAll() {
        return characterDao.findAll();
    }

    @Override
    public Character findById(Long id) {
        return characterDao.findById(id)
                .orElseThrow(() -> new BusinessException("人物不存在，ID: " + id, HttpStatus.NOT_FOUND));
    }

    @Override
    public Character findByName(String name) {
        return characterDao.findByName(name)
                .orElseThrow(() -> new BusinessException("人物不存在: " + name, HttpStatus.NOT_FOUND));
    }

    @Override
    public List<Character> findDefaultCharacters() {
        return characterDao.findByIsDefaultTrue();
    }

    @Override
    public List<Character> findPurchasableCharacters() {
        return characterDao.findByIsDefaultFalse();
    }
}
