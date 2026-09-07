package com.batuhan.bankingapi.controller;

import com.batuhan.bankingapi.dto.AccountResponse;
import com.batuhan.bankingapi.dto.AccountTransactionResponse;
import com.batuhan.bankingapi.dto.DepositRequest;
import com.batuhan.bankingapi.dto.RecipientLookupResponse;
import com.batuhan.bankingapi.dto.TransferRequest;
import com.batuhan.bankingapi.dto.WithdrawRequest;
import com.batuhan.bankingapi.entity.Account;
import com.batuhan.bankingapi.entity.Transaction;
import com.batuhan.bankingapi.entity.User;
import com.batuhan.bankingapi.mapper.AccountMapper;
import com.batuhan.bankingapi.mapper.TransactionMapper;
import com.batuhan.bankingapi.service.AccountService;
import com.batuhan.bankingapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;

    public AccountController(
            AccountService accountService,
            UserService userService
    ) {
        this.accountService = accountService;
        this.userService = userService;
    }

    // =========================================================
    // CREATE ACCOUNT
    // =========================================================

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            Principal principal
    ) {

        Account account =
                accountService.createAccount(
                        principal.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        AccountMapper.toResponse(
                                account
                        )
                );
    }

    // =========================================================
    // MY ACCOUNTS
    // =========================================================

    @GetMapping("/me")
    public List<AccountResponse> getMyAccounts(
            Principal principal
    ) {

        User user =
                userService.getUserByEmail(
                        principal.getName()
                );

        return accountService
                .getAccountsByUserId(
                        user.getId()
                )
                .stream()
                .map(AccountMapper::toResponse)
                .toList();
    }

    // =========================================================
    // RECIPIENT LOOKUP
    // =========================================================

    @GetMapping("/recipient")
    public RecipientLookupResponse getRecipient(
            @RequestParam String accountNumber
    ) {

        Account account =
                accountService
                        .getAccountByAccountNumber(
                                accountNumber
                        );

        return new RecipientLookupResponse(
                account.getAccountNumber(),
                account
                        .getUser()
                        .getFullName()
        );
    }

    // =========================================================
    // ACCOUNT DETAIL
    // =========================================================

    @GetMapping("/{id}")
    public AccountResponse getAccountById(
            @PathVariable Long id,
            Principal principal
    ) {

        Account account =
                accountService.getOwnedAccount(
                        id,
                        principal.getName()
                );

        return AccountMapper.toResponse(
                account
        );
    }

    // =========================================================
    // DEPOSIT
    // =========================================================

    @PostMapping("/{id}/deposit")
    public AccountTransactionResponse deposit(
            @PathVariable Long id,
            @Valid @RequestBody DepositRequest request,
            Principal principal
    ) {

        Transaction transaction =
                accountService.deposit(
                        id,
                        request.getAmount(),
                        principal.getName()
                );

        return TransactionMapper
                .toAccountResponse(
                        transaction,
                        id
                );
    }

    // =========================================================
    // WITHDRAW
    // =========================================================

    @PostMapping("/{id}/withdraw")
    public AccountTransactionResponse withdraw(
            @PathVariable Long id,
            @Valid @RequestBody WithdrawRequest request,
            Principal principal
    ) {

        Transaction transaction =
                accountService.withdraw(
                        id,
                        request.getAmount(),
                        principal.getName()
                );

        return TransactionMapper
                .toAccountResponse(
                        transaction,
                        id
                );
    }

    // =========================================================
    // TRANSFER
    // =========================================================

    @PostMapping("/transfer")
    public AccountTransactionResponse transfer(
            @Valid @RequestBody TransferRequest request,
            Principal principal
    ) {

        Transaction transaction =
                accountService.transfer(
                        request.getFromAccountId(),
                        request.getToAccountNumber(),
                        request.getAmount(),
                        principal.getName(),
                        request.getDescription()
                );

        return TransactionMapper
                .toAccountResponse(
                        transaction,
                        request.getFromAccountId()
                );
    }
}