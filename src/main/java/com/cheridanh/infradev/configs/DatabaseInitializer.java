package com.cheridanh.infradev.configs;

import com.cheridanh.infradev.entities.Role;
import com.cheridanh.infradev.entities.User;
import com.cheridanh.infradev.repositories.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_FIRSTNAME}")
    private String adminFirstName;

    @Value("${ADMIN_LASTNAME}")
    private String adminLastName;

    @Value("${ADMIN_EMAIL}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    @Override
    public void run(String @NonNull ... args) {
        log.info("Initialisation de la base de données");
        initializeAdminUser();
    }

    private void initializeAdminUser() {
        if (!userRepository.existsByEmail(adminEmail)) {
            log.info("Création du compte administrateur");

            User admin = User.builder()
                    .firstName(adminFirstName)
                    .lastName(adminLastName)
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .enabled(true)
                    .build();

            userRepository.save(admin);
            log.info("Compte administrateur créé avec succès");
        } else {
            log.info("Le compte administrateur existe déjà, initialisation ignorée");
        }
    }
}
