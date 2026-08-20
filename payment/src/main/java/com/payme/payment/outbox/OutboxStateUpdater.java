package com.payme.payment.outbox;

import com.payme.payment.model.OutboxEvent;
import com.payme.payment.repository.OutboxEventRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxStateUpdater {

    private final OutboxEventRepo outboxEventRepository;
    private static final int MAX_RETRIES = 5;

    @Transactional
    public void markPublished(OutboxEvent event) {
        event.setPublished(true);
        outboxEventRepository.save(event);
    }

    @Transactional
    public void handleFailure(OutboxEvent event, Exception e) {
        int retries = event.getRetryCount() == null ? 0 : event.getRetryCount();
        event.setRetryCount(retries + 1);

        if (retries + 1 >= MAX_RETRIES) {
            log.error("Outbox event {} failed after {} retries, giving up for now: {}",
                    event.getId(), retries + 1, e.getMessage());
        } else {
            log.warn("Outbox event {} failed (attempt {}), will retry: {}",
                    event.getId(), retries + 1, e.getMessage());
        }

        outboxEventRepository.save(event);
    }
}