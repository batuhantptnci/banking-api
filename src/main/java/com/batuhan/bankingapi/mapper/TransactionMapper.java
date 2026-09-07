package com.batuhan.bankingapi.mapper;

import com.batuhan.bankingapi.dto.AccountTransactionResponse;
import com.batuhan.bankingapi.dto.TransactionDirection;
import com.batuhan.bankingapi.entity.Account;
import com.batuhan.bankingapi.entity.Transaction;
import com.batuhan.bankingapi.entity.TransactionType;

import java.math.BigDecimal;

public final class TransactionMapper {

    private static final String CHANNEL = "IBT Mobil";
    private static final String STATUS = "COMPLETED";

    private TransactionMapper() {
    }

    public static AccountTransactionResponse toAccountResponse(
            Transaction transaction,
            Long currentAccountId
    ) {

        Account sourceAccount =
                transaction.getAccount();

        Account targetAccount =
                transaction.getTargetAccount();

        TransactionDirection direction =
                resolveDirection(
                        transaction,
                        currentAccountId
                );

        BigDecimal balanceAfter =
                resolveBalanceAfter(
                        transaction,
                        currentAccountId
                );

        return new AccountTransactionResponse(
                transaction.getId(),
                transaction.getType(),
                direction,
                transaction.getAmount(),

                sourceAccount.getId(),
                sourceAccount.getAccountNumber(),
                sourceAccount
                        .getUser()
                        .getFullName(),

                targetAccount != null
                        ? targetAccount.getId()
                        : null,

                targetAccount != null
                        ? targetAccount.getAccountNumber()
                        : null,

                targetAccount != null
                        ? targetAccount
                        .getUser()
                        .getFullName()
                        : null,

                balanceAfter,
                resolveDescription(transaction),
                CHANNEL,
                STATUS,
                transaction.getCreatedAt()
        );
    }

    private static TransactionDirection resolveDirection(
            Transaction transaction,
            Long currentAccountId
    ) {

        if (transaction.getType()
                == TransactionType.DEPOSIT) {

            return TransactionDirection.INCOMING;
        }

        if (transaction.getType()
                == TransactionType.WITHDRAW) {

            return TransactionDirection.OUTGOING;
        }

        Account targetAccount =
                transaction.getTargetAccount();

        if (targetAccount != null
                && targetAccount
                .getId()
                .equals(currentAccountId)) {

            return TransactionDirection.INCOMING;
        }

        return TransactionDirection.OUTGOING;
    }

    private static BigDecimal resolveBalanceAfter(
            Transaction transaction,
            Long currentAccountId
    ) {

        if (transaction.getType()
                != TransactionType.TRANSFER) {

            return transaction
                    .getSourceBalanceAfter();
        }

        Account targetAccount =
                transaction.getTargetAccount();

        if (targetAccount != null
                && targetAccount
                .getId()
                .equals(currentAccountId)) {

            return transaction
                    .getTargetBalanceAfter();
        }

        return transaction
                .getSourceBalanceAfter();
    }

    private static String resolveDescription(
            Transaction transaction
    ) {

        String description =
                transaction.getDescription();

        if (description != null
                && !description.isBlank()) {

            return description;
        }

        return switch (transaction.getType()) {

            case DEPOSIT ->
                    "IBT Mobil üzerinden para yatırma";

            case WITHDRAW ->
                    "IBT Mobil üzerinden para çekme";

            case TRANSFER ->
                    "IBT Bank hesaplar arası para transferi";
        };
    }
}