package com.batuhan.bankingapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSavedRecipientRequest {

    @NotBlank(
            message = "Hesap numarası boş olamaz"
    )
    @Pattern(
            regexp = "(?i)^ACC-[A-Z0-9]{8}$",
            message = "Geçerli bir hesap numarası giriniz"
    )
    private String accountNumber;

    @Size(
            max = 40,
            message = "Alıcı adı en fazla 40 karakter olabilir"
    )
    private String nickname;
}