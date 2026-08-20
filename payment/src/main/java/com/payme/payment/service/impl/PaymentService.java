package com.payme.payment.service.impl;

import com.payme.payment.comm.WalletServiceClient;
import com.payme.payment.dto.PaymentReq;
import com.payme.payment.dto.PaymentStatus;
import com.payme.payment.model.Payment;
import com.payme.payment.outbox.PaymentFinalizer;
import com.payme.payment.repository.PaymentRepository;
import com.payme.payment.service.PaymentImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
@RequiredArgsConstructor
public class PaymentService implements PaymentImpl {

    private final PaymentRepository paymentRepository;
    private final PaymentFinalizer paymentFinalizer;
    private final WalletServiceClient walletServiceClient;


    @Override
    public Payment getById(String paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
    }


    @Override
    public Payment initiatePayment(String key, String senderId, String senderRole, PaymentReq req) {
        var existing = paymentRepository.findByIdempotencyKey(key);
        if (existing.isPresent()) return existing.get();

        Payment payment = Payment.createPending(key, senderId, req.receiverId(), req.amount());
        try {
            payment = paymentRepository.save(payment);
        } catch (DataIntegrityViolationException e) {
            return paymentRepository.findByIdempotencyKey(key)
                    .orElseThrow(() -> new IllegalStateException("Idempotency conflict but record not found", e));
        }

        return process(payment, senderRole);
    }

    private Payment process(Payment p,String senderRole){

        p.setStatus(PaymentStatus.PROCESSING);
        paymentRepository.save(p);

        //1 . debit payer - from own wallet so allow

        String senderWalletId;
        String receiverWalletId;
        try{
            senderWalletId = walletServiceClient.getWalletIdForUser(p.getSenderId(),p.getSenderId(),senderRole);
            receiverWalletId = walletServiceClient.getWalletIdForUser(p.getReceiverId(),p.getSenderId(),senderRole);
        }catch (RestClientException e) {
           return paymentFinalizer.finalizeAsFailed(p,"Wallet resolution failed: " + e.getMessage());
        }

        try {
            walletServiceClient.debit(senderWalletId, p.getAmount(), p.getSenderId(), senderRole);
        } catch (RestClientException e) {
            return paymentFinalizer.finalizeAsFailed(p, "Debit failed: " + e.getMessage());
        }

        try {
            walletServiceClient.credit(receiverWalletId, p.getAmount(), p.getSenderId(), senderRole);
        } catch (RestClientException e) {
            return paymentFinalizer.finalizeAsFailedPartial(p, "Debit succeeded, credit failed: " + e.getMessage());
        }

        return paymentFinalizer.finalizeAsCompleted(p);
    }
}
