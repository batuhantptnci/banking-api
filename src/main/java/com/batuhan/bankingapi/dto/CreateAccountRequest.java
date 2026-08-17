package com.batuhan.bankingapi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccountRequest {

    @NotNull(message = "Kullanıcı ID boş olamaz")
    private Long userId;
}