package com.batuhan.bankingapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferRequest {

    @NotNull(message = "Gönderen hesap ID boş olamaz")
    private Long fromAccountId;

    @NotBlank(message = "Alıcı hesap numarası boş olamaz")
    @Pattern(
            regexp = "(?i)^ACC-[A-Z0-9]{8}$",
            message = "Geçerli bir hesap numarası giriniz"
    )
    private String toAccountNumber;

    @NotNull(message = "Tutar boş olamaz")
    @Positive(message = "Tutar 0'dan büyük olmalıdır")
    private BigDecimal amount;
}