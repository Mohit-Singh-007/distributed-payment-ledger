package com.payme.payment.repository;

import com.payme.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,String> {

    Optional<Payment> findByIdempotencyKey(String key);
}
