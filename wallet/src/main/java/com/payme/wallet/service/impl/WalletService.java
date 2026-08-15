package com.payme.wallet.service.impl;

import com.payme.wallet.model.Wallet;
import com.payme.wallet.repository.WalletRepository;
import com.payme.wallet.service.WalletServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class WalletService implements WalletServiceImpl {

    private final WalletRepository walletRepository;
    private final WalletTransactionExecutor walletTransactionExecutor;
    private static final int MAX_RETRIES = 3;

    @Override
    public Wallet getWalletById(String walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));
    }

    @Override
    public Wallet getByUserId(String userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for user"));
    }

    @Override
    public Wallet createWallet(String userId, String currency) {
        Wallet wallet = Wallet.createNew(userId, currency);
        return walletRepository.save(wallet);
    }

    @Override
    public Wallet credit(String walletId, BigDecimal amount) {
        return executeWithRetry(() -> walletTransactionExecutor.doCredit(walletId,amount));
    }

    @Override
    public Wallet debit(String walletId, BigDecimal amount) {
        return executeWithRetry(()->walletTransactionExecutor.doDebit(walletId,amount));
    }


    // optimistic lock and retry
    private Wallet executeWithRetry(Supplier<Wallet> operation){
        int attempts=0;

        while(true){
            try{
                return operation.get();
            }catch (OptimisticLockingFailureException e){
                attempts++;
                if(attempts >= MAX_RETRIES){
                    throw new IllegalStateException("Could not complete operation after " + MAX_RETRIES + " attempts, please retry", e);
                }
            }
        }
    }

}
