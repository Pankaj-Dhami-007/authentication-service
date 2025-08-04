package com.dhami.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class OtpService {

    private static final int OTP_LENGTH = 6;
    private static final Duration OTP_TTL = Duration.ofMinutes(2);
    private final SecureRandom secureRandom = new SecureRandom();
    private final StringRedisTemplate redisTemplate;

    public String generateOtp() {
        int number = secureRandom.nextInt((int) Math.pow(10, OTP_LENGTH)); // 0 to 999999 for 6 digits
        return String.format("%0" + OTP_LENGTH + "d", number); // Pad with zeros
    }

    /*
     Helper to generate Redis keys
     otp:login: is a prefix to avoid key collisions and organize keys.
     */
    private String redisKey(String phoneNumber) {
        return "otp:login:" + phoneNumber;
    }

    // Cache the OTP in Redis
    public void cacheOtp(String phoneNumber, String otp) {
        redisTemplate.opsForValue().set(redisKey(phoneNumber), otp, OTP_TTL);
        System.out.println("Saved OTP to Redis: key=" + redisKey(phoneNumber) + ", value=" + otp);
    }

    public String generateAndCacheOtp(String phoneNumber) {
        String otp = generateOtp();
        cacheOtp(phoneNumber, otp);
        return otp;
    }

    //  OTP verification logic
    public boolean verifyOtp(String phoneNumber, String otp) {
        String key = redisKey(phoneNumber);
        String cachedOtp = redisTemplate.opsForValue().get(key);
        if (cachedOtp != null && cachedOtp.equals(otp)) {
            redisTemplate.delete(key); // One-time use
            return true;
        }
        return false;
    }
}
