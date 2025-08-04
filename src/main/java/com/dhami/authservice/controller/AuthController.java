package com.dhami.authservice.controller;

import com.dhami.authservice.dto.request.LoginRequestDto;
import com.dhami.authservice.dto.request.SendOtpRequest;
import com.dhami.authservice.dto.request.SignupRequest;
import com.dhami.authservice.dto.request.VerifyOtpRequest;
import com.dhami.authservice.dto.response.LoginResponseDto;
import com.dhami.authservice.dto.response.SendOtpResponse;
import com.dhami.authservice.dto.response.SignupResponse;
import com.dhami.authservice.dto.response.VerifyOtpResponse;
import com.dhami.authservice.service.AuthService;
import com.dhami.authservice.service.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.dhami.authservice.constants.UrlConstants.*;

@RestController
@RequestMapping(BASE)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final VerificationTokenService verificationTokenService;

    @PostMapping(SIGNUP)
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest request) {
        try {
            SignupResponse response = authService.registerUser(request);
            // Return success message; email verification instructions should be sent
            return ResponseEntity.ok("User registered successfully! Please verify your email.");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping(VERIFY_EMAIL)
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        boolean isVerified = verificationTokenService.validateToken(token);
        if (isVerified) {
            return ResponseEntity.ok("Your account has been verified successfully!");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired verification token.");
        }
    }

    @PostMapping(LOGIN)
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto response = authService.authenticate(loginRequestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping(SEND_OTP)
    public ResponseEntity<SendOtpResponse> sendOtp(@RequestBody SendOtpRequest request){
       SendOtpResponse sendOtpResponse = authService.sendOtp(request);
       return new ResponseEntity<>(sendOtpResponse, HttpStatus.OK);
    }

    @PostMapping(VERIFY_OTP)
    public ResponseEntity<VerifyOtpResponse> verifyOtp(@RequestBody VerifyOtpRequest request){
        VerifyOtpResponse response = authService.verifyOtp(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
