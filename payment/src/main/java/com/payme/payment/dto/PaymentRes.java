package com.payme.payment.dto;

import java.math.BigDecimal;

public record PaymentRes(
        String id,
        String payerId,
        String payeeId,
        BigDecimal amount,
        String status,
        String failureReason
) {}
