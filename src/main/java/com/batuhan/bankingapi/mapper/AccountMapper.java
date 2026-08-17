package com.batuhan.bankingapi.mapper;

import com.batuhan.bankingapi.dto.AccountResponse;
import com.batuhan.bankingapi.entity.Account;

public class AccountMapper {

    public static AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getUser().getId()
        );
    }
}