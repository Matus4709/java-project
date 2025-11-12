package com.example.medappoint.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import com.example.medappoint.dto.PatientDto;
import com.example.medappoint.dto.PatientRegistrationDto;

import com.example.medappoint.exception.EmailAlreadyExistsException;

import com.example.medappoint.model.Patient;
import com.example.medappoint.model.User;
import com.example.medappoint.model.enums.UserRole;
import com.example.medappoint.repository.PatientRepository;
import com.example.medappoint.repository.UserRepository;

// Importy statyczne (dla czystszego kodu)
import static org.junit.jupiter.api.Assertions.*; // Dla asercji (sprawdzeń) np. assertEquals, assertThrows
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any; // Dopasuj DOWOLNY obiekt (np. dowolnego Usera)
import static org.mockito.ArgumentMatchers.anyString; // Dopasuj DOWOLNY String
import static org.mockito.Mockito.verify; // Sprawdź, czy metoda została wywołana
import static org.mockito.Mockito.when; // "Kiedy" (when) coś się stanie, "wtedy" (then) coś zrób
import static org.mockito.Mockito.never; // Sprawdź, czy metoda NIGDY nie została wywołana

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {
    @Mock
    private PatientRepository patientRepository;

    @Mock UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PatientService patientService;

    private PatientRegistrationDto registrationDto;
    private User existingUser;
    private Patient savedPatient;

    @BeforeEach
    void setUp() {
        registrationDto = new PatientRegistrationDto();
        registrationDto.setEmail("test@example.com");
        registrationDto.setPassword("password123");
        registrationDto.setFirstName("Jan");
        registrationDto.setLastName("Testowy");

        existingUser = User.builder()
                .email("test@example.com")
                .build();

        User newUserForPatient = User.builder()
                .email("test@example.com")
                .passwordHash("szyfrowanehaslo")
                .role(UserRole.ROLE_PATIENT)
                .build();

        savedPatient = Patient.builder()
                .id(java.util.UUID.randomUUID())
                .firstName("Jan")
                .lastName("Testowy")
                .user(newUserForPatient)
                .build();
    }

    @Test
    void registerPatient_ShouldSuccessed_WhenEmailIsNew(){
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(anyString()))
            .thenReturn("szyfrowanehaslo");

        when(patientRepository.save(any(Patient.class)))
                .thenReturn(savedPatient);

        PatientDto resultDto = patientService.registerPatient(registrationDto);

        assertNotNull(resultDto);
        assertEquals(resultDto.getFirstName(), resultDto.getFirstName());
        assertEquals(resultDto.getLastName(), resultDto.getLastName());
        assertEquals("test@example.com", resultDto.getEmail());
        assertEquals("ROLE_PATIENT", resultDto.getRole());

        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).encode(anyString());
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void registerPatient_ShouldThrowException_WhenEmailIsAlreadyExists(){
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(existingUser));

        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> {
                    patientService.registerPatient(registrationDto);
                });
        assertEquals("Email already taken: test@example.com", exception.getMessage());

        verify(passwordEncoder, never()).encode(anyString());
        verify(patientRepository, never()).save(any(Patient.class));
    }
}
