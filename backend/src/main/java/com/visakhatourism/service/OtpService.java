package com.visakhatourism.service;

import com.visakhatourism.entity.OtpPurpose;
import com.visakhatourism.entity.OtpVerification;
import com.visakhatourism.exception.BadRequestException;
import com.visakhatourism.repository.OtpVerificationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OtpService {

    private final OtpVerificationRepository otpRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.otp.expiration-minutes:5}")
    private int expirationMinutes;

    @Value("${app.otp.resend-cooldown-seconds:60}")
    private int resendCooldownSeconds;

    @Value("${app.otp.max-attempts:5}")
    private int maxAttempts;

    public OtpService(OtpVerificationRepository otpRepository,
                      EmailService emailService,
                      PasswordEncoder passwordEncoder) {
        this.otpRepository = otpRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void generateAndSendOtp(String email, OtpPurpose purpose) {
        // Check cooldown
        Optional<OtpVerification> existingOtpOpt = otpRepository.findTopByEmailAndPurposeAndUsedFalseOrderByCreatedAtDesc(email, purpose);
        if (existingOtpOpt.isPresent()) {
            OtpVerification existing = existingOtpOpt.get();
            if (existing.getResendAfter().isAfter(LocalDateTime.now())) {
                long remainingSeconds = java.time.Duration.between(LocalDateTime.now(), existing.getResendAfter()).getSeconds();
                throw new BadRequestException("Please wait " + Math.max(remainingSeconds, 1) + " seconds before requesting a new OTP.");
            }
            // Invalidate prior OTP
            existing.setUsed(true);
            otpRepository.save(existing);
        }

        // Generate 6-digit cryptographically secure OTP
        int otpNumber = 100000 + secureRandom.nextInt(900000);
        String rawOtp = String.valueOf(otpNumber);

        // Hash OTP for secure database storage
        String otpHash = passwordEncoder.encode(rawOtp);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(expirationMinutes);
        LocalDateTime resendAfter = now.plusSeconds(resendCooldownSeconds);

        OtpVerification otpRecord = new OtpVerification(
                email.toLowerCase().trim(),
                otpHash,
                purpose,
                expiresAt,
                resendAfter,
                maxAttempts
        );
        otpRepository.save(otpRecord);

        // Send email
        emailService.sendOtpEmail(email, rawOtp, purpose.name());
    }

    @Transactional
    public boolean verifyOtp(String email, String rawOtp, OtpPurpose purpose) {
        String normalizedEmail = email.toLowerCase().trim();
        OtpVerification otp = otpRepository.findTopByEmailAndPurposeAndUsedFalseOrderByCreatedAtDesc(normalizedEmail, purpose)
                .orElseThrow(() -> new BadRequestException("No active verification code found for this email. Please request a new code."));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            otp.setUsed(true);
            otpRepository.save(otp);
            throw new BadRequestException("The verification code has expired. Please request a new code.");
        }

        if (otp.getAttemptCount() >= otp.getMaxAttempts()) {
            otp.setUsed(true);
            otpRepository.save(otp);
            throw new BadRequestException("Too many incorrect verification attempts. This code has been invalidated for security. Please request a new one.");
        }

        if (!passwordEncoder.matches(rawOtp, otp.getOtpHash())) {
            otp.setAttemptCount(otp.getAttemptCount() + 1);
            otpRepository.save(otp);
            int attemptsLeft = otp.getMaxAttempts() - otp.getAttemptCount();
            throw new BadRequestException("Invalid verification code. " + attemptsLeft + " attempt(s) remaining.");
        }

        // Mark code as used
        otp.setUsed(true);
        otpRepository.save(otp);
        return true;
    }
}
