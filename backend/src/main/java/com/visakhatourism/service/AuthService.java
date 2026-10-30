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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

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
    public void register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Password and confirm password do not match");
        }

        String normalizedEmail = request.getEmail().toLowerCase().trim();
        Optional<User> existingUserOpt = userRepository.findByEmail(normalizedEmail);

        if (existingUserOpt.isPresent()) {
            User existing = existingUserOpt.get();
            if (existing.isEmailVerified()) {
                throw new BadRequestException("An account with this email address already exists. Please log in.");
            } else {
                // Update pending unverified account credentials
                existing.setName(request.getName().trim());
                existing.setPasswordHash(passwordEncoder.encode(request.getPassword()));
                userRepository.save(existing);
            }
        } else {
            // Create pending unverified user with strictly ROLE_USER
            User newUser = new User(
                    request.getName().trim(),
                    normalizedEmail,
                    passwordEncoder.encode(request.getPassword()),
                    Role.ROLE_USER,
                    false, // emailVerified = false
                    true   // enabled = true
            );
            userRepository.save(newUser);
        }

        // Trigger OTP generation and email
        otpService.generateAndSendOtp(normalizedEmail, OtpPurpose.REGISTRATION);
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

        if (request.getPurpose() == OtpPurpose.REGISTRATION && userOpt.get().isEmailVerified()) {
            throw new BadRequestException("This account is already verified. Please log in.");
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
        // Generic return to prevent email enumeration
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
