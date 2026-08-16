package com.payme.users.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    @GetMapping
    public ResponseEntity<String> admin(){
        return ResponseEntity.ok("This is admin page");
    }
    @GetMapping("/test")
    public ResponseEntity<String> adminOnly() {
        return ResponseEntity.ok("Welcome, admin.");
    }
}
