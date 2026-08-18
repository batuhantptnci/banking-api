package com.batuhan.bankingapi.controller;

import com.batuhan.bankingapi.dto.AccountResponse;
import com.batuhan.bankingapi.dto.DepositRequest;
import com.batuhan.bankingapi.dto.TransferRequest;
import com.batuhan.bankingapi.dto.WithdrawRequest;
import com.batuhan.bankingapi.entity.Account;
import com.batuhan.bankingapi.entity.User;
import com.batuhan.bankingapi.mapper.AccountMapper;
import com.batuhan.bankingapi.service.AccountService;
import com.batuhan.bankingapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

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

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            Principal principal
    ) {

        Account account = accountService.createAccount(
                principal.getName()
        );

        return ResponseEntity
                .status(201)
                .body(AccountMapper.toResponse(account));
    }

    @GetMapping("/me")
    public List<AccountResponse> getMyAccounts(Principal principal) {

        User user = userService.getUserByEmail(principal.getName());

        return accountService.getAccountsByUserId(user.getId())
                .stream()
                .map(AccountMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public AccountResponse getAccountById(
            @PathVariable Long id,
            Principal principal
    ) {
        Account account = accountService.getOwnedAccount(
                id,
                principal.getName()
        );

        return AccountMapper.toResponse(account);
    }

    @PostMapping("/{id}/deposit")
    public AccountResponse deposit(
            @PathVariable Long id,
            @Valid @RequestBody DepositRequest request,
            Principal principal
    ) {
        Account account = accountService.deposit(
                id,
                request.getAmount(),
                principal.getName()
        );

        return AccountMapper.toResponse(account);
    }

    @PostMapping("/{id}/withdraw")
    public AccountResponse withdraw(
            @PathVariable Long id,
            @Valid @RequestBody WithdrawRequest request,
            Principal principal
    ) {
        Account account = accountService.withdraw(
                id,
                request.getAmount(),
                principal.getName()
        );

        return AccountMapper.toResponse(account);
    }

    @PostMapping("/transfer")
    public ResponseEntity<Map<String, String>> transfer(
            @Valid @RequestBody TransferRequest request,
            Principal principal
    ) {

        accountService.transfer(
                request.getFromAccountId(),
                request.getToAccountId(),
                request.getAmount(),
                principal.getName()
        );

        return ResponseEntity.ok(
                Map.of("message", "Transfer başarıyla tamamlandı")
        );
    }

}