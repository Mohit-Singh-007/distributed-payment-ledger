package com.payme.users.service;

import com.payme.users.model.RefreshToken;

public interface RefreshTokenImpl {
    String issueRefreshToken(String userId);
    RefreshToken validateAndConsume(String rawToken);

}
