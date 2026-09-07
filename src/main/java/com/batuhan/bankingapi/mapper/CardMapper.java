package com.batuhan.bankingapi.mapper;

import com.batuhan.bankingapi.dto.CardResponse;
import com.batuhan.bankingapi.entity.Card;

public final class CardMapper {

    private CardMapper() {
    }

    public static CardResponse toResponse(
            Card card
    ) {

        return new CardResponse(
                card.getId(),

                card.getCardType(),

                card.getCardStatus(),

                card.getLastFour(),

                "•••• •••• •••• "
                        + card.getLastFour(),

                card.getExpiryMonth(),

                card.getExpiryYear(),

                card
                        .getUser()
                        .getFullName(),

                card
                        .getAccount()
                        .getId(),

                card
                        .getAccount()
                        .getAccountNumber(),

                card
                        .getAccount()
                        .getBalance(),

                card.getCreatedAt()
        );
    }
}