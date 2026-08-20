package com.payme.payment.dto;

import java.math.BigDecimal;

public record PaymentEventPayload(
        String paymentId, String senderId, String receiverId,
        BigDecimal amount, String status, String failureReason
) {}