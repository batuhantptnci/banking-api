package com.batuhan.bankingapi.service;

import com.batuhan.bankingapi.dto.CreateSavedRecipientRequest;
import com.batuhan.bankingapi.dto.SavedRecipientResponse;
import com.batuhan.bankingapi.entity.Account;
import com.batuhan.bankingapi.entity.SavedRecipient;
import com.batuhan.bankingapi.entity.User;
import com.batuhan.bankingapi.exception.AccountNotFoundException;
import com.batuhan.bankingapi.exception.InvalidTransferException;
import com.batuhan.bankingapi.exception.SavedRecipientAlreadyExistsException;
import com.batuhan.bankingapi.exception.SavedRecipientNotFoundException;
import com.batuhan.bankingapi.repository.AccountRepository;
import com.batuhan.bankingapi.repository.SavedRecipientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SavedRecipientService {

    private final SavedRecipientRepository
            savedRecipientRepository;

    private final AccountRepository
            accountRepository;

    private final UserService userService;

    public SavedRecipientService(
            SavedRecipientRepository savedRecipientRepository,
            AccountRepository accountRepository,
            UserService userService
    ) {
        this.savedRecipientRepository =
                savedRecipientRepository;

        this.accountRepository =
                accountRepository;

        this.userService =
                userService;
    }

    public List<SavedRecipientResponse>
    getMyRecipients(
            String userEmail
    ) {

        User owner =
                userService.getUserByEmail(
                        userEmail
                );

        return savedRecipientRepository
                .findByOwnerIdOrderByCreatedAtDesc(
                        owner.getId()
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public SavedRecipientResponse saveRecipient(
            String userEmail,
            CreateSavedRecipientRequest request
    ) {

        User owner =
                userService.getUserByEmail(
                        userEmail
                );

        String accountNumber =
                request
                        .getAccountNumber()
                        .trim()
                        .toUpperCase();

        Account recipientAccount =
                accountRepository
                        .findByAccountNumber(
                                accountNumber
                        )
                        .orElseThrow(
                                () ->
                                        new AccountNotFoundException(
                                                "Alıcı hesap bulunamadı"
                                        )
                        );

        if (recipientAccount
                .getUser()
                .getId()
                .equals(owner.getId())) {

            throw new InvalidTransferException(
                    "Kendi hesabını kayıtlı alıcı olarak ekleyemezsin"
            );
        }

        boolean alreadyExists =
                savedRecipientRepository
                        .existsByOwnerIdAndRecipientAccountId(
                                owner.getId(),
                                recipientAccount.getId()
                        );

        if (alreadyExists) {

            throw new SavedRecipientAlreadyExistsException(
                    "Bu alıcı zaten kayıtlı"
            );
        }

        SavedRecipient savedRecipient =
                new SavedRecipient();

        savedRecipient.setOwner(owner);

        savedRecipient.setRecipientAccount(
                recipientAccount
        );

        savedRecipient.setNickname(
                normalizeNickname(
                        request.getNickname(),
                        recipientAccount
                                .getUser()
                                .getFullName()
                )
        );

        return toResponse(
                savedRecipientRepository.save(
                        savedRecipient
                )
        );
    }

    @Transactional
    public void deleteRecipient(
            Long id,
            String userEmail
    ) {

        User owner =
                userService.getUserByEmail(
                        userEmail
                );

        SavedRecipient savedRecipient =
                savedRecipientRepository
                        .findByIdAndOwnerId(
                                id,
                                owner.getId()
                        )
                        .orElseThrow(
                                () ->
                                        new SavedRecipientNotFoundException(
                                                "Kayıtlı alıcı bulunamadı"
                                        )
                        );

        savedRecipientRepository.delete(
                savedRecipient
        );
    }

    private SavedRecipientResponse toResponse(
            SavedRecipient savedRecipient
    ) {

        Account account =
                savedRecipient
                        .getRecipientAccount();

        return new SavedRecipientResponse(
                savedRecipient.getId(),
                savedRecipient.getNickname(),
                account.getAccountNumber(),
                account
                        .getUser()
                        .getFullName(),
                savedRecipient.getCreatedAt()
        );
    }

    private String normalizeNickname(
            String nickname,
            String fullName
    ) {

        if (nickname != null
                && !nickname.trim().isEmpty()) {

            return nickname.trim();
        }

        String fallback =
                fullName.trim();

        if (fallback.length() > 40) {
            return fallback.substring(0, 40);
        }

        return fallback;
    }
}