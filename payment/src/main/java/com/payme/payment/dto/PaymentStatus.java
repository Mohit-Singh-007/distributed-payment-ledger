package com.payme.payment.dto;

public enum PaymentStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    FAILED_PARTIAL, //later for saga
    CANCELLED
}
