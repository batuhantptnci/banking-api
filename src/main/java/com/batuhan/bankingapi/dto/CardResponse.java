package com.batuhan.bankingapi.dto;

import com.batuhan.bankingapi.entity.CardStatus;
import com.batuhan.bankingapi.entity.CardType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CardResponse {

    private Long id;

    private CardType cardType;

    private CardStatus cardStatus;

    private String lastFour;

    private String maskedNumber;

    private Integer expiryMonth;

    private Integer expiryYear;

    private String holderName;

    private Long accountId;

    private String accountNumber;

    private BigDecimal accountBalance;

    private LocalDateTime createdAt;
}