package com.visakhatourism;

import com.visakhatourism.entity.OtpPurpose;
import com.visakhatourism.entity.OtpVerification;
import com.visakhatourism.exception.BadRequestException;
import com.visakhatourism.repository.OtpVerificationRepository;
import com.visakhatourism.service.EmailService;
import com.visakhatourism.service.OtpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OtpServiceTest {

    @Mock
    private OtpVerificationRepository otpRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private OtpService otpService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(otpService, "expirationMinutes", 5);
        ReflectionTestUtils.setField(otpService, "resendCooldownSeconds", 60);
        ReflectionTestUtils.setField(otpService, "maxAttempts", 5);
    }

    @Test
    void testVerifyOtpSuccess() {
        String email = "traveler@example.com";
        String rawOtp = "123456";
        String hashedOtp = "$2a$10$hashedOtpValue";

        OtpVerification otp = new OtpVerification(
                email, hashedOtp, OtpPurpose.REGISTRATION,
                LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().plusSeconds(60),
                5
        );

        when(otpRepository.findTopByEmailAndPurposeAndUsedFalseOrderByCreatedAtDesc(email, OtpPurpose.REGISTRATION))
                .thenReturn(Optional.of(otp));
        when(passwordEncoder.matches(rawOtp, hashedOtp)).thenReturn(true);

        boolean verified = otpService.verifyOtp(email, rawOtp, OtpPurpose.REGISTRATION);

        assertTrue(verified);
        assertTrue(otp.isUsed(), "OTP must be marked as used upon verification");
        verify(otpRepository).save(otp);
    }

    @Test
    void testVerifyOtpExpired() {
        String email = "traveler@example.com";
        String rawOtp = "123456";

        OtpVerification expiredOtp = new OtpVerification(
                email, "someHash", OtpPurpose.REGISTRATION,
                LocalDateTime.now().minusMinutes(1), // expired 1 min ago
                LocalDateTime.now().minusMinutes(4),
                5
        );

        when(otpRepository.findTopByEmailAndPurposeAndUsedFalseOrderByCreatedAtDesc(email, OtpPurpose.REGISTRATION))
                .thenReturn(Optional.of(expiredOtp));

        assertThrows(BadRequestException.class, () -> {
            otpService.verifyOtp(email, rawOtp, OtpPurpose.REGISTRATION);
        });
    }

    @Test
    void testVerifyOtpTooManyAttempts() {
        String email = "traveler@example.com";
        String rawOtp = "999999";

        OtpVerification otp = new OtpVerification(
                email, "someHash", OtpPurpose.REGISTRATION,
                LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().plusSeconds(60),
                5
        );
        otp.setAttemptCount(5); // reached limit

        when(otpRepository.findTopByEmailAndPurposeAndUsedFalseOrderByCreatedAtDesc(email, OtpPurpose.REGISTRATION))
                .thenReturn(Optional.of(otp));

        assertThrows(BadRequestException.class, () -> {
            otpService.verifyOtp(email, rawOtp, OtpPurpose.REGISTRATION);
        });
    }
}
