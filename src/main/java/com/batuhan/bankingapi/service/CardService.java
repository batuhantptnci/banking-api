package com.batuhan.bankingapi.service;

import com.batuhan.bankingapi.dto.CardResponse;
import com.batuhan.bankingapi.entity.Account;
import com.batuhan.bankingapi.entity.Card;
import com.batuhan.bankingapi.entity.CardStatus;
import com.batuhan.bankingapi.entity.CardType;
import com.batuhan.bankingapi.entity.User;
import com.batuhan.bankingapi.exception.CardAlreadyExistsException;
import com.batuhan.bankingapi.exception.CardNotFoundException;
import com.batuhan.bankingapi.exception.InvalidCardOperationException;
import com.batuhan.bankingapi.mapper.CardMapper;
import com.batuhan.bankingapi.repository.CardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class CardService {

    private final CardRepository cardRepository;

    private final UserService userService;

    private final AccountService accountService;

    private final SecureRandom secureRandom =
            new SecureRandom();

    public CardService(
            CardRepository cardRepository,
            UserService userService,
            AccountService accountService
    ) {
        this.cardRepository =
                cardRepository;

        this.userService =
                userService;

        this.accountService =
                accountService;
    }

    public List<CardResponse> getMyCards(
            String userEmail
    ) {

        User user =
                userService.getUserByEmail(
                        userEmail
                );

        return cardRepository
                .findByUserIdAndCardStatusNotOrderByCreatedAtDesc(
                        user.getId(),
                        CardStatus.CLOSED
                )
                .stream()
                .map(CardMapper::toResponse)
                .toList();
    }

    @Transactional
    public CardResponse createDebitCard(
            Long accountId,
            String userEmail
    ) {

        Account account =
                accountService.getOwnedAccount(
                        accountId,
                        userEmail
                );

        boolean openCardExists =
                cardRepository
                        .existsByAccountIdAndCardStatusNot(
                                accountId,
                                CardStatus.CLOSED
                        );

        if (openCardExists) {

            throw new CardAlreadyExistsException(
                    "Bu hesaba bağlı aktif bir banka kartı zaten mevcut"
            );
        }

        YearMonth expiry =
                YearMonth
                        .now()
                        .plusYears(5);

        Card card =
                new Card();

        card.setUser(
                account.getUser()
        );

        card.setAccount(account);

        card.setCardType(
                CardType.DEBIT
        );

        card.setCardStatus(
                CardStatus.ACTIVE
        );

        card.setLastFour(
                generateLastFour()
        );

        card.setExpiryMonth(
                expiry.getMonthValue()
        );

        card.setExpiryYear(
                expiry.getYear()
        );

        card.setClosedAt(null);

        return CardMapper.toResponse(
                cardRepository.save(card)
        );
    }

    @Transactional
    public CardResponse freezeCard(
            Long cardId,
            String userEmail
    ) {

        Card card =
                getOwnedCard(
                        cardId,
                        userEmail
                );

        if (card.getCardStatus()
                == CardStatus.CLOSED) {

            throw new InvalidCardOperationException(
                    "Kapalı kart dondurulamaz"
            );
        }

        if (card.getCardStatus()
                == CardStatus.FROZEN) {

            throw new InvalidCardOperationException(
                    "Kart zaten dondurulmuş"
            );
        }

        card.setCardStatus(
                CardStatus.FROZEN
        );

        return CardMapper.toResponse(
                cardRepository.save(card)
        );
    }

    @Transactional
    public CardResponse unfreezeCard(
            Long cardId,
            String userEmail
    ) {

        Card card =
                getOwnedCard(
                        cardId,
                        userEmail
                );

        if (card.getCardStatus()
                == CardStatus.CLOSED) {

            throw new InvalidCardOperationException(
                    "Kapalı kart tekrar aktifleştirilemez"
            );
        }

        if (card.getCardStatus()
                == CardStatus.ACTIVE) {

            throw new InvalidCardOperationException(
                    "Kart zaten aktif"
            );
        }

        card.setCardStatus(
                CardStatus.ACTIVE
        );

        return CardMapper.toResponse(
                cardRepository.save(card)
        );
    }

    @Transactional
    public void closeCard(
            Long cardId,
            String userEmail
    ) {

        Card card =
                getOwnedCard(
                        cardId,
                        userEmail
                );

        if (card.getCardStatus()
                == CardStatus.CLOSED) {

            throw new InvalidCardOperationException(
                    "Kart zaten kapatılmış"
            );
        }

        card.setCardStatus(
                CardStatus.CLOSED
        );

        card.setClosedAt(
                LocalDateTime.now()
        );

        cardRepository.save(card);
    }

    private Card getOwnedCard(
            Long cardId,
            String userEmail
    ) {

        User user =
                userService.getUserByEmail(
                        userEmail
                );

        return cardRepository
                .findByIdAndUserId(
                        cardId,
                        user.getId()
                )
                .orElseThrow(
                        () ->
                                new CardNotFoundException(
                                        "Kart bulunamadı"
                                )
                );
    }

    private String generateLastFour() {

        int number =
                secureRandom.nextInt(
                        10_000
                );

        return String.format(
                "%04d",
                number
        );
    }
}