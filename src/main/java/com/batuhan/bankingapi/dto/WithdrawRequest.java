package com.batuhan.bankingapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class WithdrawRequest {

    @NotNull(message = "Tutar boş olamaz")
    @Positive(message = "Tutar 0'dan büyük olmalıdır")
    private BigDecimal amount;
}