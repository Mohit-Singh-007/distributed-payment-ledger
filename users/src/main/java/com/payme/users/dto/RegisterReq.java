package com.payme.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterReq(
        @Size(max = 24)
        String username,

        @Email
        @NotBlank
        String email,

        @Size(min = 8, max = 24)
        @NotBlank
        String password
) {}
