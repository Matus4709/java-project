package com.example.medappoint.config;

import com.example.medappoint.model.Doctor;
import com.example.medappoint.model.User;
import com.example.medappoint.model.enums.UserRole;
import com.example.medappoint.repository.DoctorRepository;
import com.example.medappoint.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initializeDoctor();
    }

    @Transactional
    private void initializeDoctor() {
        String doctorEmail = "doctor@example.com";
        
        // Sprawdź czy doktor już istnieje
        if (userRepository.findByEmail(doctorEmail).isPresent()) {
            log.info("Konto doktora już istnieje: {}", doctorEmail);
            return;
        }

        // Utwórz użytkownika doktora (nie zapisujemy osobno - cascade zrobi to za nas)
        User doctorUser = User.builder()
                .email(doctorEmail)
                .passwordHash(passwordEncoder.encode("doctor123"))
                .role(UserRole.ROLE_DOCTOR)
                .build();

        // Utwórz profil doktora - cascade zapisze również User
        Doctor doctor = Doctor.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .specialization("Kardiolog")
                .description("Doświadczony kardiolog z 15-letnim stażem")
                .user(doctorUser)
                .build();

        doctorRepository.save(doctor);

        log.info("Utworzono konto doktora:");
        log.info("Email: {}", doctorEmail);
        log.info("Hasło: doctor123");
        log.info("Imię: Jan, Nazwisko: Kowalski");
        log.info("Specjalizacja: Kardiolog");
    }
}

