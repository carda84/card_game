package com.cardgame.dao;

import com.cardgame.model.entity.ShopItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopItemDao extends JpaRepository<ShopItem, Long> {

    List<ShopItem> findByStockGreaterThan(int minStock);

    Optional<ShopItem> findByCardId(Long cardId);
}
