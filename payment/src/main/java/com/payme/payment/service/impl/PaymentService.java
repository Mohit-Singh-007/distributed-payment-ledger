package com.payme.payment.service.impl;

import com.payme.payment.comm.WalletServiceClient;
import com.payme.payment.dto.PaymentReq;
import com.payme.payment.dto.PaymentStatus;
import com.payme.payment.model.Payment;
import com.payme.payment.repository.PaymentRepository;
import com.payme.payment.service.PaymentImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
@RequiredArgsConstructor
public class PaymentService implements PaymentImpl {

    private final PaymentRepository paymentRepository;
    private final WalletServiceClient walletServiceClient;


    @Override
    public Payment getById(String paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
    }

    @Override
    public Payment initiatePayment(String key, String payerId, String payerRole, PaymentReq req) {
        // idempotency
        var existing = paymentRepository.findByIdempotencyKey(key);
        if(existing.isPresent()) return existing.get();

        Payment payment = Payment.createPending(key,payerId,req.payeeId(),req.amount());
        paymentRepository.save(payment);

        return process(payment,payerRole);

    }
    private Payment process(Payment payment,String payerRole){

        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);

        //1 . debit payer - from own wallet so allow

        try{
            walletServiceClient.debit(payment.getPayerId(),payment.getAmount(),payment.getPayerId(),payerRole);
        }catch (RestClientException e) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Debit failed: " + e.getMessage());
            return paymentRepository.save(payment);
        }

        //2. credit payee — will currently fail ownership check (flagged, addressed later)
        try {
            walletServiceClient.credit(payment.getPayeeId(), payment.getAmount(), payment.getPayerId(), payerRole);
        } catch (RestClientException e) {
            payment.setStatus(PaymentStatus.FAILED_PARTIAL);
            payment.setFailureReason("Debit succeeded, credit failed: " + e.getMessage());
            return paymentRepository.save(payment);
        }

        payment.setStatus(PaymentStatus.COMPLETED);
        return paymentRepository.save(payment);
    }
}
