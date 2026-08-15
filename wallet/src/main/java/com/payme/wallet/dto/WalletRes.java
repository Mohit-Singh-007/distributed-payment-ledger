package com.payme.wallet.dto;

import java.math.BigDecimal;

public record WalletRes(
        String id,
        String userId,
        BigDecimal balance,
        String currency
) {
}
