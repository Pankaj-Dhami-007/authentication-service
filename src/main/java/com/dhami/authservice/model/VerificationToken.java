package com.dhami.authservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity(name = "verification_tokens")
@Getter
@Setter
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The unique token string, typically a UUID
    @Column(nullable = false, unique = true)
    private String token;

    // Token expiry date and time
    @Column(nullable = false)
    private LocalDateTime expiryDate;

    // Link to the user this token belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // Default constructor for JPA
    public VerificationToken() {}

    // Constructor for creating new tokens
    public VerificationToken(String token, LocalDateTime expiryDate, UserEntity user) {
        this.token = token;
        this.expiryDate = expiryDate;
        this.user = user;
    }

    // Check if token is expired
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
}
