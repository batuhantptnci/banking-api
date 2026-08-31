package com.batuhan.bankingapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Müşteri no / T.C. kimlik no boş olamaz")
    @Pattern(
            regexp = "^(\\d{8}|\\d{11})$",
            message = "8 haneli müşteri no veya 11 haneli T.C. kimlik no giriniz"
    )
    private String identifier;

    @NotBlank(message = "Şifre boş olamaz")
    private String password;
}