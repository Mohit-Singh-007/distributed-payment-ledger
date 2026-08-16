package com.payme.payment.comm;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient walletServiceRestClient(){
        return RestClient.builder()
                .baseUrl("http://localhost:8082")
                .build();
    }
}
