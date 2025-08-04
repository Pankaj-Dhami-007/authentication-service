package com.dhami.authservice.service;

import com.dhami.authservice.model.UserEntity;
import com.dhami.authservice.model.VerificationToken;
import com.dhami.authservice.repository.UserRepository;
import com.dhami.authservice.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class VerificationTokenService {

    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    public VerificationToken createToken(UserEntity user) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(24); // token valid 24 hours
        VerificationToken verificationToken = new VerificationToken(token, expiryDate, user);
        return tokenRepository.save(verificationToken);
    }

    public boolean validateToken(String token) {
        Optional<VerificationToken> optionalToken = tokenRepository.findByToken(token);
        if (optionalToken.isEmpty()) {
            return false;
        }

        VerificationToken verificationToken = optionalToken.get();

        if (verificationToken.isExpired()) {
            return false;
        }

        UserEntity user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user); // enable user

        tokenRepository.delete(verificationToken); // remove used token

        return true;
    }

}
