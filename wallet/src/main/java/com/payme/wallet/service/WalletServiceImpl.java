package com.payme.wallet.service;

import com.payme.wallet.model.Wallet;

import java.math.BigDecimal;

public interface WalletServiceImpl {
    Wallet createWallet(String userId, String currency);
    Wallet credit(String walletId, BigDecimal amount);
    Wallet debit(String walletId,BigDecimal amount);
}
