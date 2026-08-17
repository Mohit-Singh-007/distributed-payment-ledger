package com.payme.wallet.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InternalAuth {

    @Value("${internal.api-key}")
    private String internalApiKey;

    public boolean isInternalCall(HttpServletRequest request){
        String header = request.getHeader("X-Internal-Api-Key");
        return internalApiKey.equals(header);
    }
}
