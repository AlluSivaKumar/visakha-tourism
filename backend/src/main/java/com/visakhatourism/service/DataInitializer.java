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
        if (userRepository.countByRole(Role.ROLE_ADMIN) == 0) {
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
                logger.info("Initial Administrator bootstrapped successfully!");
                logger.info("Admin Email: {}", normalizedEmail);
                logger.info("Role       : ROLE_ADMIN");
                logger.info("==========================================================");
            }
        }
    }
}
