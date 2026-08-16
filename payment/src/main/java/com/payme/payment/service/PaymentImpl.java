package com.payme.payment.service;

import com.payme.payment.dto.PaymentReq;
import com.payme.payment.model.Payment;

public interface PaymentImpl {
    Payment initiatePayment(String key, String payerId, String payeeId, PaymentReq req);
    Payment getById(String paymentId);

}
