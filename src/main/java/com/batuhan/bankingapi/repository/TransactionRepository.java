package com.batuhan.bankingapi.repository;

import com.batuhan.bankingapi.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountIdOrTargetAccountIdOrderByCreatedAtDesc(
            Long accountId,
            Long targetAccountId
    );
}
