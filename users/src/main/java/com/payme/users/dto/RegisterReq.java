package com.payme.users.dto;

public record RegisterReq(
        String username,
        String email,
        String password
) {}
