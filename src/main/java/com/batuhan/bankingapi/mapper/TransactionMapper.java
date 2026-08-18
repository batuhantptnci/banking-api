package com.batuhan.bankingapi.mapper;


import com.batuhan.bankingapi.entity.Transaction;
import com.batuhan.bankingapi.dto.AccountTransactionResponse;
import com.batuhan.bankingapi.dto.TransactionDirection;
import com.batuhan.bankingapi.entity.TransactionType;

public class TransactionMapper {

    public static AccountTransactionResponse toAccountResponse(
            Transaction transaction,
            Long currentAccountId
    ) {

        TransactionDirection direction;

        if (transaction.getType() == TransactionType.DEPOSIT) {

            direction = TransactionDirection.INCOMING;

        } else if (transaction.getType() == TransactionType.WITHDRAW) {

            direction = TransactionDirection.OUTGOING;

        } else {

            if (transaction.getTargetAccount().getId().equals(currentAccountId)) {
                direction = TransactionDirection.INCOMING;
            } else {
                direction = TransactionDirection.OUTGOING;
            }
        }

        Long targetAccountId = transaction.getTargetAccount() != null
                ? transaction.getTargetAccount().getId()
                : null;

        return new AccountTransactionResponse(
                transaction.getId(),
                transaction.getType(),
                direction,
                transaction.getAmount(),
                transaction.getAccount().getId(),
                targetAccountId,
                transaction.getCreatedAt()
        );
    }
}