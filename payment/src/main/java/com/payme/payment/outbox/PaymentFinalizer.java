package com.payme.payment.outbox;

// creating separate cauz if I call @Transactional method in same class , it bypasses proxy and no ACID is maintained

import com.payme.payment.dto.PaymentEventPayload;
import com.payme.payment.dto.PaymentStatus;
import com.payme.payment.model.OutboxEvent;
import com.payme.payment.model.Payment;
import com.payme.payment.repository.OutboxEventRepo;
import com.payme.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class PaymentFinalizer {

    private final PaymentRepository paymentRepository;
    private final OutboxEventRepo outboxEventRepo;
    private final ObjectMapper objectMapper;

    @Transactional
    public Payment finalizeAsCompleted(Payment p){
        p.setStatus(PaymentStatus.COMPLETED);
        Payment res = paymentRepository.save(p);
        saveOutboxEvent(res,PaymentEventTypes.PAYMENT_COMPLETED);
        return res;
    }


    @Transactional
    public Payment finalizeAsFailed(Payment payment, String reason) {
        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(reason);
        Payment saved = paymentRepository.save(payment);
        saveOutboxEvent(saved, PaymentEventTypes.PAYMENT_FAILED);
        return saved;
    }


    @Transactional
    public Payment finalizeAsFailedPartial(Payment payment,String reason){
        payment.setStatus(PaymentStatus.FAILED_PARTIAL);
        payment.setFailureReason(reason);
        Payment saved = paymentRepository.save(payment);
        saveOutboxEvent(saved, PaymentEventTypes.PAYMENT_FAILED_PARTIAL);
        return saved;
    }

    private void saveOutboxEvent(Payment p,String eventType){

        try{
            String payload = objectMapper.writeValueAsString(
                    new PaymentEventPayload(
                            p.getId(),
                            p.getSenderId(),
                            p.getReceiverId(),
                            p.getAmount(),
                            p.getStatus().name(),
                            p.getFailureReason()
                    )
            );
            outboxEventRepo.save(OutboxEvent.create(p.getId(),eventType,payload));
        }catch (Exception e) {
            throw new IllegalStateException("Failed to serialize outbox event payload", e);
        }
    }
}
