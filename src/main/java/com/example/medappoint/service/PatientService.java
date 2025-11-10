package com.example.medappoint.service;

import com.example.medappoint.model.Patient;
import com.example.medappoint.model.User;
import com.example.medappoint.model.enums.UserRole;
import com.example.medappoint.dto.PatientDto;
import com.example.medappoint.dto.PatientRegistrationDto;
import com.example.medappoint.exception.EmailAlreadyExistsException;
import com.example.medappoint.repository.PatientRepository;
import com.example.medappoint.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public PatientDto registerPatient(PatientRegistrationDto registrationDto) {

        userRepository.findByEmail(registrationDto.getEmail()).ifPresent(user -> {
            throw new EmailAlreadyExistsException("Email already taken: " + user.getEmail());
        });

        User newUser = User.builder()
                .email(registrationDto.getEmail())
                .passwordHash(passwordEncoder.encode(registrationDto.getPassword())) // Hashujemy hasło!
                .role(UserRole.ROLE_PATIENT)
                .build();

        Patient newPatient = Patient.builder()
                .firstName(registrationDto.getFirstName())
                .lastName(registrationDto.getLastName())
                .pesel(registrationDto.getPesel())
                .phoneNumber(registrationDto.getPhoneNumber())
                .user(newUser)
                .build();

        Patient savedPatient = patientRepository.save(newPatient);


        return toPatientDto(savedPatient);
    }

    private PatientDto toPatientDto(Patient patient) {
        return PatientDto.builder()
                .id(patient.getId())
                .email(patient.getUser().getEmail())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .pesel(patient.getPesel())
                .phoneNumber(patient.getPhoneNumber())
                .role(patient.getUser().getRole().name())
                .build();
    }
}
