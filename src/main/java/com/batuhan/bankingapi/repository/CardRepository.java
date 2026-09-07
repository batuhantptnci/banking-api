package com.batuhan.bankingapi.repository;

import com.batuhan.bankingapi.entity.Card;
import com.batuhan.bankingapi.entity.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository
        extends JpaRepository<Card, Long> {

    List<Card>
    findByUserIdAndCardStatusNotOrderByCreatedAtDesc(
            Long userId,
            CardStatus cardStatus
    );

    Optional<Card> findByIdAndUserId(
            Long id,
            Long userId
    );

    boolean existsByAccountIdAndCardStatusNot(
            Long accountId,
            CardStatus cardStatus
    );
}