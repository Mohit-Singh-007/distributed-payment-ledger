package com.payme.payment.dto;

import java.math.BigDecimal;

public record AmountReq(
        BigDecimal amount
) {
}
