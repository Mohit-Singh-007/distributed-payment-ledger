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
    public Payment initiatePayment(String key, String senderId, String senderRole, PaymentReq req) {
        // idempotency
        var existing = paymentRepository.findByIdempotencyKey(key);
        if(existing.isPresent()) return existing.get();

        Payment payment = Payment.createPending(key,senderId,req.receiverId(),req.amount());
        paymentRepository.save(payment);

        return process(payment,senderRole);

    }
    private Payment process(Payment payment,String senderRole){

        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);

        //1 . debit payer - from own wallet so allow

        String senderWalletId;
        String recieverWalletId;
        try{
            senderWalletId = walletServiceClient.getWalletIdForUser(payment.getSenderId(),payment.getSenderId(),senderRole);
            recieverWalletId = walletServiceClient.getWalletIdForUser(payment.getReceiverId(),payment.getSenderId(),senderRole);
        }catch (RestClientException e) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Wallet resolution failed: " + e.getMessage());
            return paymentRepository.save(payment);
        }

        try {
            walletServiceClient.debit(senderWalletId, payment.getAmount(), payment.getSenderId(), senderRole);
        } catch (RestClientException e) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Debit failed: " + e.getMessage());
            return paymentRepository.save(payment);
        }

        try {
            walletServiceClient.credit(recieverWalletId, payment.getAmount(), payment.getSenderId(), senderRole);
        } catch (RestClientException e) {
            payment.setStatus(PaymentStatus.FAILED_PARTIAL);
            payment.setFailureReason("Debit succeeded, credit failed: " + e.getMessage());
            return paymentRepository.save(payment);
        }

        payment.setStatus(PaymentStatus.COMPLETED);
        return paymentRepository.save(payment);
    }
}
