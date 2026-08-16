package com.payme.wallet.controller;

import com.payme.wallet.dto.*;
import com.payme.wallet.model.Wallet;
import com.payme.wallet.service.impl.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<WalletRes> create(@Valid @RequestBody CreateWalletReq req, Authentication auth){

        Wallet wallet = walletService.createWallet(auth.getName(), req.currency());
        return ResponseEntity.status(HttpStatus.CREATED).body(toRes(wallet));
    }


    @GetMapping("/{walletId}")
    @PreAuthorize("@walletService.isOwner(#walletId, authentication.name)")
    public ResponseEntity<WalletRes> get(@PathVariable String walletId) {
        Wallet wallet = walletService.getWalletById(walletId);
        return ResponseEntity.ok(toRes(wallet));
    }

    @GetMapping("/me")
    public ResponseEntity<WalletRes> getMyWallet(Authentication auth) {
        Wallet wallet = walletService.getByUserId(auth.getName());
        return ResponseEntity.ok(toRes(wallet));
    }

    @PostMapping("/{walletId}/credit")
    @PreAuthorize("@walletService.isOwner(#walletId, authentication.name)")
    public ResponseEntity<WalletRes> credit(@PathVariable String walletId, @RequestBody CreditReq req) {
        Wallet updated = walletService.credit(walletId, req.amount());
        return ResponseEntity.ok(toRes(updated));
    }

    @PostMapping("/{walletId}/debit")
    @PreAuthorize("@walletService.isOwner(#walletId, authentication.name)")
    public ResponseEntity<WalletRes> debit(@PathVariable String walletId, @RequestBody DebitReq req) {
        Wallet updated = walletService.debit(walletId, req.amount());
        return ResponseEntity.ok(toRes(updated));
    }

    // internal call
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<WalletIdRes> getUserById(@PathVariable String userId){
        Wallet wallet = walletService.getByUserId(userId);
        return ResponseEntity.ok(new WalletIdRes(wallet.getId()));
    }



    private WalletRes toRes(Wallet wallet) {
        return new WalletRes(wallet.getId(), wallet.getUserId(), wallet.getBalance(), wallet.getCurrency());
    }
}
