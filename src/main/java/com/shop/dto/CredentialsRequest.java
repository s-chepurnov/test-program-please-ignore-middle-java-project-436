package com.shop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CredentialsRequest(
        @NotBlank(message = "email: must not be blank")
        @Email(message = "email: must be a valid address")
        String email,

        @NotBlank(message = "password: must not be blank")
        @Size(min = 6, message = "password: must be at least 6 characters")
        String password
) {}
