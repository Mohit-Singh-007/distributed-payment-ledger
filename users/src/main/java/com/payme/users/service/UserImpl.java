package com.payme.users.service;

import com.payme.users.dto.*;
import org.springframework.security.core.Authentication;

public interface UserImpl {
    LoginRes login(LoginReq req);
    void register(RegisterReq req);
    UserRes me(Authentication auth);

    LoginRes refresh(RefreshReq req);
}
