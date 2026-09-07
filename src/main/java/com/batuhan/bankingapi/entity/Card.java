package com.batuhan.bankingapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "cards",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_cards_account",
                        columnNames = "account_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @OneToOne(optional = false)
    @JoinColumn(
            name = "account_id",
            nullable = false
    )
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "card_type",
            nullable = false,
            length = 20
    )
    private CardType cardType =
            CardType.DEBIT;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "card_status",
            nullable = false,
            length = 20
    )
    private CardStatus cardStatus =
            CardStatus.ACTIVE;

    @Column(
            name = "last_four",
            nullable = false,
            length = 4
    )
    private String lastFour;

    @Column(
            name = "expiry_month",
            nullable = false
    )
    private Integer expiryMonth;

    @Column(
            name = "expiry_year",
            nullable = false
    )
    private Integer expiryYear;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt =
            LocalDateTime.now();
}