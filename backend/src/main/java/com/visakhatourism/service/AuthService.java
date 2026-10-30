package com.visakhatourism.service;

import com.visakhatourism.dto.AuthDtos.*;
import com.visakhatourism.entity.OtpPurpose;
import com.visakhatourism.entity.Role;
import com.visakhatourism.entity.User;
import com.visakhatourism.exception.BadRequestException;
import com.visakhatourism.exception.ResourceNotFoundException;
import com.visakhatourism.exception.UnauthorizedException;
import com.visakhatourism.repository.UserRepository;
import com.visakhatourism.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository,
                       OtpService otpService,
                       EmailService emailService,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.otpService = otpService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Password and confirm password do not match");
        }

        String normalizedEmail = request.getEmail().toLowerCase().trim();
        Optional<User> existingUserOpt = userRepository.findByEmail(normalizedEmail);

        if (existingUserOpt.isPresent()) {
            throw new BadRequestException("An account with this email address already exists. Please log in.");
        }

        // Create verified traveler user with strictly ROLE_USER
        User newUser = new User(
                request.getName().trim(),
                normalizedEmail,
                passwordEncoder.encode(request.getPassword()),
                Role.ROLE_USER,
                true, // Instant email verification for smooth traveler sign-up
                true  // enabled = true
        );
        userRepository.save(newUser);

        // Attempt non-blocking background notification
        try {
            otpService.generateAndSendOtp(normalizedEmail, OtpPurpose.REGISTRATION);
        } catch (Exception ex) {
            logger.warn("Background OTP notification skipped: {}", ex.getMessage());
        }

        String token = tokenProvider.generateToken(newUser);
        return new AuthResponse(token, newUser.getId(), newUser.getName(), newUser.getEmail(), newUser.getRole());
    }

    @Transactional
    public AuthResponse googleLogin(Map<String, String> payload) {
        String email = payload.get("email");
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Google account email is required");
        }
        String normalizedEmail = email.toLowerCase().trim();
        String name = payload.getOrDefault("name", "Traveler").trim();
        if (name.isBlank()) name = "Traveler";

        Optional<User> userOpt = userRepository.findByEmail(normalizedEmail);
        User user;
        if (userOpt.isPresent()) {
            user = userOpt.get();
            if (!user.isEmailVerified()) {
                user.setEmailVerified(true);
                userRepository.save(user);
            }
        } else {
            user = new User(
                    name,
                    normalizedEmail,
                    passwordEncoder.encode(UUID.randomUUID().toString()),
                    Role.ROLE_USER,
                    true, // Instant Google verification
                    true  // enabled
            );
            user = userRepository.save(user);
        }

        String token = tokenProvider.generateToken(user);
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    @Transactional
    public AuthResponse verifyEmail(VerifyOtpRequest request) {
        String normalizedEmail = request.getEmail().toLowerCase().trim();

        // Verify OTP
        otpService.verifyOtp(normalizedEmail, request.getOtp().trim(), OtpPurpose.REGISTRATION);

        // Mark user as verified
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User registration record not found"));

        user.setEmailVerified(true);
        userRepository.save(user);

        // Generate JWT token
        String token = tokenProvider.generateToken(user);
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    public void resendOtp(ResendOtpRequest request) {
        String normalizedEmail = request.getEmail().toLowerCase().trim();
        Optional<User> userOpt = userRepository.findByEmail(normalizedEmail);

        if (userOpt.isEmpty()) {
            // For security, do not leak user existence for reset flows
            if (request.getPurpose() == OtpPurpose.PASSWORD_RESET) {
                return;
            }
            throw new BadRequestException("No registration found with this email");
        }

        otpService.generateAndSendOtp(normalizedEmail, request.getPurpose());
    }

    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().toLowerCase().trim();
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        if (!user.isEmailVerified()) {
            throw new UnauthorizedException("Your email address is not yet verified. Please verify your account using the OTP sent to your email.");
        }

        if (!user.isEnabled()) {
            throw new UnauthorizedException("Your account has been deactivated. Please contact support.");
        }

        String token = tokenProvider.generateToken(user);
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    public AuthResponse adminLogin(LoginRequest request) {
        String normalizedEmail = request.getEmail().toLowerCase().trim();
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UnauthorizedException("Invalid admin credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid admin credentials");
        }

        if (user.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedException("Access denied. Administrator privileges are required.");
        }

        if (!user.isEnabled()) {
            throw new UnauthorizedException("Admin account is deactivated.");
        }

        String token = tokenProvider.generateToken(user);
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        String normalizedEmail = request.getEmail().toLowerCase().trim();
        Optional<User> userOpt = userRepository.findByEmail(normalizedEmail);

        if (userOpt.isPresent() && userOpt.get().isEmailVerified()) {
            otpService.generateAndSendOtp(normalizedEmail, OtpPurpose.PASSWORD_RESET);
        }
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Password and confirm password do not match");
        }

        String normalizedEmail = request.getEmail().toLowerCase().trim();

        // Verify OTP for password reset
        otpService.verifyOtp(normalizedEmail, request.getOtp().trim(), OtpPurpose.PASSWORD_RESET);

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public UserProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found"));

        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.isEmailVerified(),
                user.getCreatedAt()
        );
    }

    @Transactional
    public UserProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found"));

        user.setName(request.getName().trim());
        userRepository.save(user);

        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.isEmailVerified(),
                user.getCreatedAt()
        );
    }

    public void sendTestEmail(String to) {
        emailService.sendTestEmail(to);
    }
}
