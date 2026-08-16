package com.payme.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentReq(
        @NotBlank String receiverId,
        @NotNull @DecimalMin(value = "0.01", message = "Amount must be positive") BigDecimal amount
) {}