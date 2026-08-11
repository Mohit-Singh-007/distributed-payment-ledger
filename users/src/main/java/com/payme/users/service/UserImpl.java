package com.payme.users.service;

import com.payme.users.dto.LoginReq;
import com.payme.users.dto.RegisterReq;
import com.payme.users.dto.UserRes;
import org.springframework.security.core.Authentication;

public interface UserImpl {
    void login(LoginReq req);
    void register(RegisterReq req);
    UserRes me(Authentication auth);
}
