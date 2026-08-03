package com.cardgame.dao;

import com.cardgame.model.entity.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CharacterDao extends JpaRepository<Character, Long> {

    Optional<Character> findByName(String name);

    /** 查找所有默认人物（免费） */
    List<Character> findByIsDefaultTrue();

    /** 查找所有可购买人物 */
    List<Character> findByIsDefaultFalse();
}
