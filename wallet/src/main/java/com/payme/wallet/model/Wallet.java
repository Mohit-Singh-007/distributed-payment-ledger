package com.payme.wallet.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false , precision = 19,scale = 4)
    private BigDecimal balance;

    @Version
    private Long version;

    @Column(nullable = false,length = 3)
    private String currency;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // enforcing 0 balance wallet
    public static Wallet createNew(String userId, String currency){
        return Wallet.builder()
                .userId(userId)
                .currency(currency)
                .balance(BigDecimal.ZERO)
                .build();
    }

}
