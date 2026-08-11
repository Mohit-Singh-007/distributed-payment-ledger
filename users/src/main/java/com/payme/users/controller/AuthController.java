package com.payme.users.controller;

import com.payme.users.dto.LoginReq;
import com.payme.users.dto.LoginRes;
import com.payme.users.dto.RegisterReq;
import com.payme.users.model.User;
import com.payme.users.repository.UserRepository;
import com.payme.users.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterReq req){

        if(userRepository.existsByEmail(req.email())){
            return ResponseEntity.badRequest().body("Email already exists...");
        }

        User user = new User(
                req.username(),
                req.email(),
                passwordEncoder.encode(req.password())
        );

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully...");

    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReq req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.email(), req.password()
                )
        );
        String role = auth.getAuthorities().stream().findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow();

        String token = jwtService.generateToken(auth.getName(), role);

        return  ResponseEntity.ok(new LoginRes(token));

    }
}
