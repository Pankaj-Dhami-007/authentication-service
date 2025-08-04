package com.dhami.authservice.service;

import com.dhami.authservice.dto.request.LoginRequestDto;
import com.dhami.authservice.dto.request.SendOtpRequest;
import com.dhami.authservice.dto.request.SignupRequest;
import com.dhami.authservice.dto.request.VerifyOtpRequest;
import com.dhami.authservice.dto.response.LoginResponseDto;
import com.dhami.authservice.dto.response.SendOtpResponse;
import com.dhami.authservice.dto.response.SignupResponse;
import com.dhami.authservice.dto.response.VerifyOtpResponse;
import com.dhami.authservice.jwt.JwtService;
import com.dhami.authservice.model.UserEntity;
import com.dhami.authservice.model.VerificationToken;
import com.dhami.authservice.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final OtpService otpService;

    public SignupResponse registerUser(SignupRequest signupRequest) {

        log.info("Registering user with email: {}", signupRequest.getEmail());
        Optional<UserEntity> existingUserOpt = userRepository.findByEmail(signupRequest.getEmail());
        if (existingUserOpt.isPresent()) {
            UserEntity existingUser = existingUserOpt.get();
            if (existingUser.isEnabled()) {
                log.warn("Email already in use: {}", signupRequest.getEmail());
                throw new IllegalArgumentException("Email already in use");
            } else {
                // User exists but not verified → resend verification email
                log.info("User exists but not verified. Resending verification email to: {}", existingUser.getEmail());
                VerificationToken newToken = verificationTokenService.createToken(existingUser);
                sendVerificationEmail(existingUser.getEmail(), newToken.getToken());

                // Return a meaningful response
                return new SignupResponse("Verification email resent. Please check your inbox.");
            }
        }

        // Check username uniqueness as before
//        if(userRepository.findByUsername(signupRequest.getUsername()).isPresent()) {
//            throw new IllegalArgumentException("Username already in use");
//        }

        // Hash password
        String hashedPassword = passwordEncoder.encode(signupRequest.getPassword());
        // Create disabled user
        UserEntity user = new UserEntity(signupRequest.getUsername(), signupRequest.getEmail(), hashedPassword);
        user.setEnabled(false);

        UserEntity savedUser = userRepository.save(user);

        log.info("User saved successfully. Sending verification email to: {}", savedUser.getEmail());
        VerificationToken createdToken = verificationTokenService.createToken(savedUser);

        // Send verification email here
        sendVerificationEmail(savedUser.getEmail(), createdToken.getToken());

        return new SignupResponse("success");
    }

    private void sendVerificationEmail(String email, String verificationToken) { //TODO: Update with company logo
        String subject = "Account Verification";
        String verificationLink = "http://localhost:8080/auth/api/v1/verify?token=" + verificationToken;
        String htmlMessage = "<html>"
                + "<body>"
                + "<h2>Welcome!</h2>"
                + "<p>Please click the button or the link below to verify your email address:</p>"
                + "<a href='" + verificationLink + "' style='padding:10px 20px;background:#007bff;color:#fff;text-decoration:none;border-radius:5px;'>Verify Email</a>"
                + "<br><br>If the button does not work, copy and paste this URL into your browser:<br>"
                + verificationLink
                + "</body></html>";

        try {
            emailService.sendVerificationEmail(email, subject, htmlMessage);
        } catch (MessagingException e) {
            // Handle email sending exception
            e.printStackTrace();
        }
    }

    public LoginResponseDto authenticate(LoginRequestDto loginRequestDto){

        log.info("Authenticating user with email: {}", loginRequestDto.getEmail());
        UserEntity dbUser = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!dbUser.isEnabled()) {
            log.warn("Account not verified for user: {}", dbUser.getEmail());
            throw new RuntimeException("Account not verified. Please verify your account.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getEmail(),
                            loginRequestDto.getPassword()
                    )
            );
            log.info("Authentication successful for user: {}", loginRequestDto.getEmail());
        }
        catch (AuthenticationException ex) {
            throw new RuntimeException("Invalid email or password");
        }

        String jwtToken = jwtService.generateToken(dbUser.getEmail());
        log.info("JWT token generated for user: {}", dbUser.getEmail());
        return new LoginResponseDto(jwtToken);
    }

    public SendOtpResponse sendOtp(SendOtpRequest otpRequest){

        Optional<UserEntity> userOpt = userRepository.findByUsername(otpRequest.getPhoneNumber());
        UserEntity user;
        if (userOpt.isEmpty()){
            //create new user
             user = UserEntity.builder()
                    .username(otpRequest.getPhoneNumber())
                    .enabled(false)
                    .build();
             user = userRepository.save(user);
        }
        else {
            //user found in DB, use that
            user = userOpt.get();
        }
        String otp = otpService.generateAndCacheOtp(user.getUsername());
        // Call SMS service here

        return new SendOtpResponse("OTP has been sent successfully "+otp);
    }

    public VerifyOtpResponse verifyOtp(VerifyOtpRequest request){

        //Find user by phone number
        UserEntity user = userRepository.findByUsername(request.getPhoneNumber())
                .orElseThrow(() -> new RuntimeException("User not found in db"));

        // Verify the OTP via OtpService
        boolean isValidated = otpService.verifyOtp(user.getUsername(), request.getOtp());
        if(!isValidated) {
            throw new RuntimeException("not validate otp");
        }

        // Mark the user as enabled/verified
        if (!user.isEnabled()) {
            user.setEnabled(true);
            userRepository.save(user);
        }

        String jwtToken = jwtService.generateToken(user.getUsername());
        log.info("JWT Token generated for user: {}", user.getUsername());
        return new VerifyOtpResponse("OTP verified successfully.", jwtToken);
    }
}
