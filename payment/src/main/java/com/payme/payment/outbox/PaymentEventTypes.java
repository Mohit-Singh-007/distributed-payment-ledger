package com.payme.payment.outbox;

public class PaymentEventTypes {
    public static final String PAYMENT_COMPLETED = "PaymentCompleted";
    public static final String PAYMENT_FAILED = "PaymentFailed";
    public static final String PAYMENT_FAILED_PARTIAL = "PaymentFailedPartial";
}
