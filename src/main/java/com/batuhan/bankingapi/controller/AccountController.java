package com.batuhan.bankingapi.controller;

import com.batuhan.bankingapi.dto.AccountResponse;
import com.batuhan.bankingapi.dto.CreateAccountRequest;
import com.batuhan.bankingapi.dto.CreateUserRequest;
import com.batuhan.bankingapi.entity.Account;
import com.batuhan.bankingapi.mapper.AccountMapper;
import com.batuhan.bankingapi.service.AccountService;
import com.batuhan.bankingapi.dto.DepositRequest;
import com.batuhan.bankingapi.dto.TransferRequest;
import com.batuhan.bankingapi.dto.WithdrawRequest;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")

public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request){
        Account account = accountService.createAccount(request.getUserId());

        return ResponseEntity
                .status(201)
                .body(AccountMapper.toResponse(account));
    }
    @GetMapping
    public List<AccountResponse> getAllAccounts() {
        return accountService.getAllAccounts()
                .stream()
                .map(AccountMapper::toResponse)
                .toList();
    }

    @GetMapping("/user/{userId}")
    public List<AccountResponse> getAccountsByUserId(
            @PathVariable Long userId
    ) {
        return accountService.getAccountsByUserId(userId)
                .stream()
                .map(AccountMapper::toResponse)
                .toList();
    }
    @GetMapping("/{id}")
    public AccountResponse getAccountById(@PathVariable Long id) {

        Account account = accountService.getAccountById(id);

        return AccountMapper.toResponse(account);
    }
    @PostMapping("/{id}/deposit")
    public AccountResponse deposit(
            @PathVariable Long id,
            @Valid @RequestBody DepositRequest request
    ) {
        Account account = accountService.deposit(id, request.getAmount());

        return AccountMapper.toResponse(account);
    }
    @PostMapping("/{id}/withdraw")
    public AccountResponse withdraw(
            @PathVariable Long id,
            @Valid @RequestBody WithdrawRequest request
    ) {
        Account account = accountService.withdraw(id, request.getAmount());

        return AccountMapper.toResponse(account);
    }
    @PostMapping("/transfer")
    public ResponseEntity<Map<String, String>> transfer(
            @Valid @RequestBody TransferRequest request
    ) {

        accountService.transfer(
                request.getFromAccountId(),
                request.getToAccountId(),
                request.getAmount()
        );

        return ResponseEntity.ok(
                Map.of("message", "Transfer başarıyla tamamlandı")
        );
    }
}
