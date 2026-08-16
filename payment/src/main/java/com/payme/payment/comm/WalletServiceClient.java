package com.payme.payment.comm;

import com.payme.payment.dto.AmountReq;
import com.payme.payment.dto.WalletRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;


// later feign when eureka
@Component
@RequiredArgsConstructor
public class WalletServiceClient {
    private final RestClient restClient;


    public WalletRes debit(String walletId, BigDecimal amount,String callerId,String callerRole){

        return restClient.post()
                .uri("/wallets/{walletId}/debit",walletId)
                .header("X-User-Id",callerId)
                .header("X-User-Role",callerRole)
                .body(new AmountReq(amount))
                .retrieve().body(WalletRes.class);
    }

    public WalletRes credit(String walletId, BigDecimal amount, String callerId, String callerRole) {
        return restClient.post()
                .uri("/wallets/{walletId}/credit", walletId)
                .header("X-User-Id", callerId)
                .header("X-User-Role", callerRole)
                .body(new AmountReq(amount))
                .retrieve()
                .body(WalletRes.class);
    }


}
