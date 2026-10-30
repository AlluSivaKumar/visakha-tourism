package com.visakhatourism.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String smtpUsername;

    @Value("${spring.mail.password:}")
    private String smtpPassword;

    @Value("${app.mail.sender:sivakumarallu4@gmail.com}")
    private String mailSenderAddress;

    public EmailService(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendOtpEmail(String recipientEmail, String otp, String purpose) {
        String subject = "Visakha Tourism - " + (purpose.equalsIgnoreCase("REGISTRATION") ? "Account Verification OTP" : "Password Reset OTP");
        String htmlContent = buildOtpHtmlContent(otp, purpose);

        // Check if SMTP is realistically configured
        if (mailSender == null || !StringUtils.hasText(smtpUsername) || !StringUtils.hasText(smtpPassword)) {
            logger.warn("SMTP credentials not fully configured (username: {}, password set: {}). Logging OTP for local development.",
                    smtpUsername, StringUtils.hasText(smtpPassword));
            logger.info("==================================================================");
            logger.info("[DEV MODE EMAIL NOTIFICATION]");
            logger.info("Recipient: {}", recipientEmail);
            logger.info("Purpose  : {}", purpose);
            logger.info(">>> ONE-TIME PASSWORD (OTP): {} <<< (Valid for 5 minutes)", otp);
            logger.info("==================================================================");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailSenderAddress, "Visakha Tourism");
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Successfully sent OTP email to {} from {}", recipientEmail, mailSenderAddress);
        } catch (Exception ex) {
            logger.error("Failed to deliver live email via SMTP to {}: {}. Logging OTP as fallback.", recipientEmail, ex.getMessage(), ex);
            logger.info(">>> FALLBACK OTP for {}: {} <<<", recipientEmail, otp);
        }
    }

    private String buildOtpHtmlContent(String otp, String purpose) {
        String title = purpose.equalsIgnoreCase("REGISTRATION") ? "Welcome to Visakha Tourism!" : "Password Reset Request";
        String message = purpose.equalsIgnoreCase("REGISTRATION")
                ? "Thank you for joining Visakha Tourism. Please use the verification code below to activate your account and begin exploring Vizag."
                : "We received a request to reset your Visakha Tourism account password. Use the verification code below to proceed.";

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="utf-8">
                    <style>
                        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #f0fdf4; margin: 0; padding: 24px; color: #1e293b; }
                        .card { max-width: 520px; margin: 0 auto; background: #ffffff; border-radius: 16px; padding: 32px; box-shadow: 0 10px 25px rgba(0,0,0,0.06); border: 1px solid #e2e8f0; }
                        .brand { color: #0284c7; font-size: 24px; font-weight: 700; margin-bottom: 8px; }
                        .subtitle { color: #64748b; font-size: 14px; margin-bottom: 24px; }
                        .otp-box { background: #f0f9ff; border: 2px dashed #0284c7; border-radius: 12px; padding: 20px; text-align: center; margin: 24px 0; }
                        .otp-code { font-size: 36px; font-weight: 800; letter-spacing: 8px; color: #0369a1; }
                        .expiry { color: #dc2626; font-size: 13px; font-weight: 600; margin-top: 8px; }
                        .footer { margin-top: 32px; font-size: 12px; color: #94a3b8; text-align: center; line-height: 1.5; }
                    </style>
                </head>
                <body>
                    <div class="card">
                        <div class="brand">🌊 Visakha Tourism</div>
                        <div class="subtitle">City of Destiny • Jewel of the East Coast</div>
                        <h2 style="font-size: 20px; margin-top: 0;">%s</h2>
                        <p style="line-height: 1.6; color: #334155;">%s</p>
                        <div class="otp-box">
                            <div class="otp-code">%s</div>
                            <div class="expiry">Expires in 5 minutes</div>
                        </div>
                        <p style="font-size: 13px; color: #64748b; line-height: 1.5;">
                            Security tip: Do not share this OTP with anyone. Visakha Tourism staff will never ask for your verification code.
                            If you did not request this, you can safely ignore this email.
                        </p>
                        <div class="footer">
                            Visakha Tourism • Andhra Pradesh Tourism Initiative<br>
                            Visakhapatnam, Andhra Pradesh, India
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(title, message, otp);
    }
}
