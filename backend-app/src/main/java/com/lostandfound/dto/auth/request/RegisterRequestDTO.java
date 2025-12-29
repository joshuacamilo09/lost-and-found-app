package com.lostandfound.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO(
        @NotBlank String username,
        @Email String email,
        @NotBlank String password
) {}
