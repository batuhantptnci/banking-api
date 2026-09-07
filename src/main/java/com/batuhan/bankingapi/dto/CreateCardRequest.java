package com.batuhan.bankingapi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCardRequest {

    @NotNull(
            message = "Hesap seçimi zorunludur"
    )
    private Long accountId;
}