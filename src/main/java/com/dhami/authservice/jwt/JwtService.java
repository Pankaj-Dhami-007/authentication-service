package com.dhami.authservice.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret_key;

    @Value("${jwt.expiration.hours}")
    private int expirationHours;

    /*
     Generate JWT Token
     */
    public String generateToken(String username){

        Instant now = Instant.now();
        Instant expiryInstant = now.plus(Duration.ofHours(expirationHours));
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiryInstant))
                .signWith(SignatureAlgorithm.HS256, secret_key)
                .compact();
    }

    /*
     Validate JWT Token
     Tries to parse the token using the secret key.
     If parsing is successful and token is not expired → returns true, If invalid or expired → returns false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secret_key)
                    .parseClaimsJws(token);
            return !isTokenExpired(token); // token is valid if not expired
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Extract username from token
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Extract expiration date
    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    /*
     Check if token is expired
      What it does:
            Compares expiration date with current time
             If token is expired → returns true
     */
    private boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        return expiration.before(new Date());
    }

    /*
    Common claim extractor
    Safely parses the JWT and returns the claims (payload)
    Claims can include subject, issuedAt, expiration, and custom data
     */
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret_key)
                .parseClaimsJws(token)
                .getBody();
    }
}

/*

io.jsonwebtoken.* (jjwt library)
Class	Purpose
Jwts	Factory class to build or parse JWT tokens
Claims	A type of Map that holds token payload (username, expiration, etc.)
JwtException	Exception thrown when JWT parsing or validation fails
SignatureAlgorithm	Enum for selecting the signing algorithm (like HS256)


Used:
Instant, Duration, Date – Java 8 Date/Time
Jwts.builder() – to build a JWT
signWith(...) – signing for security

parseClaimsJws() – validates token structure + signature


        .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationHours))
 */
