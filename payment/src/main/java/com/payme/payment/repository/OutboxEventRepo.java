package com.payme.payment.repository;

import com.payme.payment.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepo extends JpaRepository<OutboxEvent,String> {
    List<OutboxEvent> findTop10ByPublishedFalseOrderByCreatedAtAsc();
}
