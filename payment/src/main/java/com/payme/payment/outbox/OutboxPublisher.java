package com.payme.payment.outbox;

import com.payme.payment.model.OutboxEvent;
import com.payme.payment.repository.OutboxEventRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxPublisher {
    private final OutboxEventRepo outboxEventRepo;
    private final OutboxStateUpdater outboxStateUpdater;
    private final KafkaTemplate<String ,String> kafkaTemplate;

    private static final String TOPIC = "payment-events";

    @Scheduled(fixedDelay = 2000)
    public void relay(){

        List<OutboxEvent> pending = outboxEventRepo.findTop10ByPublishedFalseOrderByCreatedAtAsc();

        for(OutboxEvent e : pending){
            try{
                kafkaTemplate.send(TOPIC,e.getAggregateId(),e.getPayload()).get();
                outboxStateUpdater.markPublished(e);
            }catch (Exception ex){
                outboxStateUpdater.handleFailure(e,ex);
            }
        }

    }

}
