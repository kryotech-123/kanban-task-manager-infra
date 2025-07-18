package com.amalitech.kanbantaskmanagement.service.impl;

import com.amalitech.kanbantaskmanagement.dto.response.AuthResponse;
import com.amalitech.kanbantaskmanagement.dto.request.LoginRequest;
import com.amalitech.kanbantaskmanagement.dto.response.LoginResponse;
import com.amalitech.kanbantaskmanagement.dto.request.OtpVerificationRequest;
import com.amalitech.kanbantaskmanagement.exception.InvalidCredentialsException;
import com.amalitech.kanbantaskmanagement.exception.OtpExpiredException;
import com.amalitech.kanbantaskmanagement.exception.OtpInvalidException;
import com.amalitech.kanbantaskmanagement.exception.OtpSessionNotFoundException;
import com.amalitech.kanbantaskmanagement.model.jpa.OtpSession;
import com.amalitech.kanbantaskmanagement.model.jpa.RefreshToken;
import com.amalitech.kanbantaskmanagement.model.jpa.User;
import com.amalitech.kanbantaskmanagement.repository.jpa.OtpSessionRepository;
import com.amalitech.kanbantaskmanagement.repository.jpa.RefreshTokenRepository;
import com.amalitech.kanbantaskmanagement.repository.jpa.UserRepository;
import com.amalitech.kanbantaskmanagement.security.service.JwtService;
import com.amalitech.kanbantaskmanagement.service.AuthService;
import com.amalitech.kanbantaskmanagement.service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    @Value("${app.api.otplifetime}")
    private Integer otpLifetime;

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final OtpSessionRepository otpSessionRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserDetailsService userDetailsService;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           OtpSessionRepository otpSessionRepository,
                           EmailService emailService,
                           JwtService jwtService,
                           RefreshTokenRepository refreshTokenRepository,
                           UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.otpSessionRepository = otpSessionRepository;
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userDetailsService = userDetailsService;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.usernameOrEmail(),
                            request.password()
                    ));
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        User user = userRepository.findByUsernameOrEmail(request.usernameOrEmail(), request.usernameOrEmail())
                .orElseThrow(() -> new IllegalStateException("User not found after successful authentication. Critical data integrity issue."));

        String otpCode = String.format("%04d", new Random().nextInt(10000));

        OtpSession otpSession = OtpSession.builder()
                .sessionId(UUID.randomUUID())
                .user(user)
                .otpCode(otpCode)
                .expiresAt(LocalDateTime.now().plus(otpLifetime, ChronoUnit.MINUTES))
                .isUsed(false)
                .build();

        otpSessionRepository.save(otpSession);

        emailService.sendOtpEmail(user.getEmail(), otpCode);

        String maskedEmail = maskEmail(user.getEmail());

        return new LoginResponse(
                otpSession.getSessionId().toString(),
                maskedEmail
        );
    }


    @Transactional
    public AuthResponse verifyOtp(OtpVerificationRequest request) {

        OtpSession otpSession = otpSessionRepository.findBySessionIdWithUser(request.sessionId())
                .orElseThrow(() -> new OtpSessionNotFoundException("Invalid session"));

        if (otpSession.getIsUsed()) {
            throw new OtpInvalidException("OTP has already been used.");
        }

        if (otpSession.getExpiresAt().isBefore(LocalDateTime.now())) {
            otpSession.setIsUsed(true);
            otpSessionRepository.save(otpSession);
            throw new OtpExpiredException("Invalid or expired OTP");
        }

        if (!otpSession.getOtpCode().equals(request.otp())) {
            otpSession.setIsUsed(true);
            otpSessionRepository.save(otpSession);
            throw new OtpInvalidException("Invalid or expired OTP");
        }

        otpSession.setIsUsed(true);
        otpSessionRepository.save(otpSession);

        User user = otpSession.getUser();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        String accessToken = jwtService.generateAccessToken(userDetails, user);
        String refreshTokenString = jwtService.generateRefreshToken(userDetails);

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .token(refreshTokenString)
                .user(user)
                .expiresAt(LocalDateTime.now().plus(7, ChronoUnit.DAYS))
                .isValid(true)
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        return new AuthResponse(
                accessToken,
                refreshTokenString
        );
    }

    @Transactional
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new InvalidCredentialsException("No authenticated user to log out.");
        }

        org.springframework.security.core.userdetails.User springUser =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        User user = userRepository.findByUsername(springUser.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("User not found during logout process."));


        List<RefreshToken> validRefreshTokens = refreshTokenRepository.findAllByUserAndIsValid(user, true);
        if (validRefreshTokens.isEmpty()) {
        } else {
            for (RefreshToken token : validRefreshTokens) {
                token.setIsValid(false);
                refreshTokenRepository.save(token);
            }
        }
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }

        int atIndex = email.indexOf('@');
        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        if (username.length() <= 2) {
            return "***" + domain;
        }

        return username.charAt(0) + "***" + username.charAt(username.length() - 1) + domain;
    }
}