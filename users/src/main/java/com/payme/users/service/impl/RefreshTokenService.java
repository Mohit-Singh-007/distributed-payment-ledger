package com.payme.users.service.impl;

import com.payme.users.model.RefreshToken;
import com.payme.users.repository.RefreshTokenRepository;
import com.payme.users.service.RefreshTokenImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RefreshTokenService implements RefreshTokenImpl {

    private final RefreshTokenRepository refreshTokenRepository;
    private static final long REFRESH_TOKEN_TTL_DAYS=7;

    @Override
    public String issueRefreshToken(String userId) {
        String rawToken = generateSecureToken();
        String hashToken = hash(rawToken);

        RefreshToken res = RefreshToken.builder()
                .hashedToken(hashToken)
                .userId(userId)
                .expiryDate(LocalDateTime.now().plusDays(REFRESH_TOKEN_TTL_DAYS))
                .isRevoked(false)
                .build();

        refreshTokenRepository.save(res);

        return rawToken;
    }

    @Override
    public RefreshToken validateAndConsume(String rawToken) {
        String hashedToken = hash(rawToken);

        RefreshToken storedToken = refreshTokenRepository.findByHashedToken(hashedToken)
                .orElseThrow(()-> new IllegalArgumentException("Invalid refresh token..."));

        if(storedToken.isRevoked() || storedToken.getExpiryDate().isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException("Refresh token is revoked or expired...");
        }

        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        return storedToken;

    }

    private String generateSecureToken(){
        byte[] bytes = new byte[64];
        new SecureRandom().nextBytes(bytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String rawToken) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(hashBytes);
        }catch (NoSuchAlgorithmException e){
            throw new IllegalStateException(e);
        }

    }


}
