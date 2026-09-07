package com.batuhan.bankingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SavedRecipientResponse {

    private Long id;

    private String nickname;

    private String accountNumber;

    private String fullName;

    private LocalDateTime createdAt;
}