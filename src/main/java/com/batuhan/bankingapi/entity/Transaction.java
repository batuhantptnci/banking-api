package com.batuhan.bankingapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(
            name = "account_id",
            nullable = false
    )
    private Account account;

    @ManyToOne
    @JoinColumn(name = "target_account_id")
    private Account targetAccount;

    @Column(
            name = "source_balance_after",
            precision = 19,
            scale = 2
    )
    private BigDecimal sourceBalanceAfter;

    @Column(
            name = "target_balance_after",
            precision = 19,
            scale = 2
    )
    private BigDecimal targetBalanceAfter;

    @Column(
            name = "description",
            length = 100
    )
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt =
            LocalDateTime.now();
}