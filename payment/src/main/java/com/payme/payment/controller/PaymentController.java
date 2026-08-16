package com.payme.payment.controller;

import com.payme.payment.dto.PaymentReq;
import com.payme.payment.dto.PaymentRes;
import com.payme.payment.model.Payment;
import com.payme.payment.service.impl.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<PaymentRes> create(
            @RequestHeader("Idempotency-key") String key,
            @Valid @RequestBody PaymentReq req,
            Authentication auth
            ){
        String payerRole = auth.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("USER");

        Payment payment = paymentService.initiatePayment(key, auth.getName(), payerRole, req);

        return ResponseEntity.status(HttpStatus.CREATED).body(toRes(payment));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentRes> get(@PathVariable String paymentId) {
        Payment payment = paymentService.getById(paymentId);
        return ResponseEntity.ok(toRes(payment));
    }


    private PaymentRes toRes(Payment payment) {
        return new PaymentRes(
                payment.getId(),
                payment.getPayerId(),
                payment.getPayeeId(),
                payment.getAmount(),
                payment.getStatus().name(),
                payment.getFailureReason()
        );
    }
}
