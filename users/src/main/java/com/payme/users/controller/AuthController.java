package com.payme.users.controller;

import com.payme.users.dto.LoginReq;
import com.payme.users.dto.RefreshReq;
import com.payme.users.dto.RegisterReq;
import com.payme.users.service.impl.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterReq req){
        userService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User registered successfully...");
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReq req) {
        return ResponseEntity.ok(userService.login(req));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshReq req){
        return ResponseEntity.ok(userService.refresh(req));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshReq req) {
        userService.logout(req);
        return ResponseEntity.ok("Logged out successfully");
    }


}
