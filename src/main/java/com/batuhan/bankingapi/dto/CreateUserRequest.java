package com.batuhan.bankingapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {

    @NotBlank(message = "İsim boş olamaz")
    private String fullName;

    @NotBlank(message = "T.C. kimlik numarası boş olamaz")
    @Pattern(
            regexp = "^\\d{11}$",
            message = "T.C. kimlik numarası 11 haneli olmalıdır"
    )
    private String nationalId;

    @NotBlank(message = "Telefon numarası boş olamaz")
    @Pattern(
            regexp = "^5\\d{9}$",
            message = "Telefon numarası 5 ile başlayan 10 haneli bir numara olmalıdır"
    )
    private String phone;

    @NotBlank(message = "Email boş olamaz")
    @Email(message = "Geçerli bir email adresi giriniz")
    private String email;

    @NotBlank(message = "Şifre boş olamaz")
    @Size(
            min = 8,
            message = "Şifre en az 8 karakter olmalıdır"
    )
    private String password;
}