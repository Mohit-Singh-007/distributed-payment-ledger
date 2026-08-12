package com.payme.users.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false,unique = true)
    private String tokenHash;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private boolean isRevoked = false;


}
