package com.visakhatourism.service;

import com.visakhatourism.entity.Role;
import com.visakhatourism.entity.User;
import com.visakhatourism.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.default-email:sivakumarallu4@gmail.com}")
    private String adminEmail;

    @Value("${app.admin.default-password:Admin@Vizag2026}")
    private String adminPassword;

    @Value("${app.admin.default-name:Siva Kumar Allu}")
    private String adminName;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Ensure Primary Author & Administrator: Siva Kumar Allu
        String normalizedEmail = adminEmail.toLowerCase().trim();
        if (userRepository.findByEmail(normalizedEmail).isEmpty()) {
            User admin = new User(
                    adminName,
                    normalizedEmail,
                    passwordEncoder.encode(adminPassword),
                    Role.ROLE_ADMIN,
                    true, // verified
                    true  // enabled
            );
            userRepository.save(admin);
            logger.info("==========================================================");
            logger.info("Primary Administrator bootstrapped: {} ({})", adminName, normalizedEmail);
            logger.info("Role       : ROLE_ADMIN");
            logger.info("==========================================================");
        }

        // Ensure Second Author & Administrator: Rakesh Paragada
        String rakeshEmail = "rakeshparagada2004@gmail.com";
        userRepository.findByEmail(rakeshEmail).ifPresentOrElse(
                u -> {
                    if (u.getRole() != Role.ROLE_ADMIN) {
                        u.setRole(Role.ROLE_ADMIN);
                        u.setEmailVerified(true);
                        u.setEnabled(true);
                        userRepository.save(u);
                        logger.info("Upgraded existing user to ROLE_ADMIN: Rakesh Paragada ({})", rakeshEmail);
                    }
                },
                () -> {
                    User rakesh = new User(
                            "Rakesh Paragada",
                            rakeshEmail,
                            passwordEncoder.encode(adminPassword),
                            Role.ROLE_ADMIN,
                            true, // verified
                            true  // enabled
                    );
                    userRepository.save(rakesh);
                    logger.info("==========================================================");
                    logger.info("Second Author & Administrator bootstrapped: Rakesh Paragada ({})", rakeshEmail);
                    logger.info("Role       : ROLE_ADMIN");
                    logger.info("==========================================================");
                }
        );
    }
}
