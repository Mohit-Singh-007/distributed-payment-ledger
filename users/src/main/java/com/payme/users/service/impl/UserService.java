package com.payme.users.service.impl;

import com.payme.users.dto.*;
import com.payme.users.enums.UserRole;
import com.payme.users.model.RefreshToken;
import com.payme.users.model.User;
import com.payme.users.repository.UserRepository;
import com.payme.users.security.JwtService;
import com.payme.users.service.UserImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserImpl {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;


    @Override
    public LoginRes login(LoginReq req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.email(),req.password()
                )
        );

        String role = auth.getAuthorities().stream()
                .findFirst().map(GrantedAuthority::getAuthority).orElseThrow();

        User user = userRepository.findByEmail(req.email())
                .orElseThrow();

        String accessToken = jwtService.generateToken(auth.getName(), role);
        String refreshToken = refreshTokenService.issueRefreshToken(user.getId());

        return new LoginRes(accessToken, refreshToken);

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
                .role(UserRole.USER)
                .enabled(true)
                .accountLocked(false)
                .build();

        userRepository.save(user);
    }


    @Override
    public void logout(RefreshReq req) {
        refreshTokenService.revokeToken(req.refreshToken());
    }

    @Override
    public UserRes me(Authentication auth) {
        User u = userRepository.findByEmail(auth.getName()).orElseThrow();

        return new UserRes(
                u.getId(),
                u.getEmail(),
                u.getRole().name()
        );
    }

    @Override
    public LoginRes refresh(RefreshReq req) {
        RefreshToken token;

        try{
            token = refreshTokenService.validateAndConsume(req.refreshToken());
        }catch (IllegalArgumentException e){
            throw new BadCredentialsException("Invalid or expired refresh token...");
        }

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new IllegalStateException("User not found for valid refresh token..."));

        String role = "ROLE_"+user.getRole().name();

        String newAccessToken = jwtService.generateToken(user.getEmail(),role);
        String newRefreshToken = refreshTokenService.issueRefreshToken(user.getId());

        return new LoginRes(newAccessToken, newRefreshToken);

    }

}
