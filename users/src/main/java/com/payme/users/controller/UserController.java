package com.payme.users.controller;

import com.payme.users.dto.UserRes;
import com.payme.users.model.User;
import com.payme.users.repository.UserRepository;
import com.payme.users.service.impl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

private final UserService userService;
    @GetMapping("/me")
    public ResponseEntity<UserRes> authentication(Authentication auth){

        UserRes res = userService.me(auth);

        return ResponseEntity.ok(res);
    }

}
