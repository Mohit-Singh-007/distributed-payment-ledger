package com.payme.users.service.impl;

import com.payme.users.dto.LoginReq;
import com.payme.users.dto.RegisterReq;
import com.payme.users.enums.UserRole;
import com.payme.users.model.User;
import com.payme.users.repository.UserRepository;
import com.payme.users.service.UserImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserImpl {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public void login(LoginReq req) {

    }

    @Override
    public void register(RegisterReq req) {

        if(userRepository.existsByEmail(req.email())){
            throw new IllegalArgumentException("Email already exists...");
        }

        User user = User.builder()
                .email(req.email())
                .username(req.username())
                .hashedPassword(
                        passwordEncoder.encode(req.password())
                                )
                .role(UserRole.ROLE_USER)
                .enabled(true)
                .accountLocked(false)
                .build();

        userRepository.save(user);
    }
}
