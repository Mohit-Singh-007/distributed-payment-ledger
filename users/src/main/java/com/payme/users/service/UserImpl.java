package com.payme.users.service;

import com.payme.users.dto.LoginReq;
import com.payme.users.dto.RegisterReq;

public interface UserImpl {
    void login(LoginReq req);
    void register(RegisterReq req);
}
