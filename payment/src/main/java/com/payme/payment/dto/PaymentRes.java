package com.payme.payment.dto;

import java.math.BigDecimal;

public record PaymentRes(
        String id,
        String senderId,
        String receiverId,
        BigDecimal amount,
        String status,
        String failureReason
) {}