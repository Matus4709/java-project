package com.example.medappoint.service;

import com.example.medappoint.dto.SlotCreateDto;
import com.example.medappoint.model.AvailableSlot;
import com.example.medappoint.model.Doctor;
import com.example.medappoint.model.User;
import com.example.medappoint.model.enums.UserRole;
import com.example.medappoint.repository.AvailableSlotRepository;
import com.example.medappoint.repository.DoctorRepository;
import com.example.medappoint.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any; // Import do "dowolnego" obiektu
import static org.mockito.Mockito.*; // Import dla when(), verify(), never()

@ExtendWith(MockitoExtension.class)
class SlotServiceTest {

    // --- 1. Konfiguracja ---

    // Chcemy "udawać" te trzy repozytoria (Kierowników Magazynu)
    @Mock
    private UserRepository userRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private AvailableSlotRepository availableSlotRepository;

    // To jest "Kucharz", którego testujemy.
    // @InjectMocks wstrzyknie do niego 3 powyższe mocki.
    @InjectMocks
    private SlotService slotService;

    // Zmienne pomocnicze, które będziemy resetować przed każdym testem
    private User doctorUser;
    private User patientUser;
    private Doctor doctor;
    private SlotCreateDto slotDto;

    // @BeforeEach: Uruchom tę metodę, aby przygotować "świeże" dane przed każdym testem
    @BeforeEach
    void setUp() {
        // Przygotuj "fałszywego" użytkownika-lekarza
        doctorUser = new User();
        doctorUser.setEmail("dr.house@test.pl");
        doctorUser.setRole(UserRole.ROLE_DOCTOR);

        // Przygotuj "fałszywego" użytkownika-pacjenta
        patientUser = new User();
        patientUser.setEmail("anna.nowak@test.pl");
        patientUser.setRole(UserRole.ROLE_PATIENT);

        // Przygotuj "fałszywy" profil lekarza
        doctor = new Doctor();
        doctor.setUser(doctorUser);

        // Przygotuj "fałszywe" DTO (Menu zamówienia)
        slotDto = new SlotCreateDto();
        slotDto.setStartTime(LocalDateTime.now().plusDays(1)); // Używamy przyszłej daty
        slotDto.setEndTime(LocalDateTime.now().plusDays(1).plusHours(1));
    }

    // --- 2. Testy ---

    @Test
    void createSlot_ShouldSucceed_WhenUserIsDoctor() {
        // --- ARRANGE (Ustawienia) ---
        // Co mają robić nasze "fałszywe" repozytoria?

        // 1. "KIEDY 'userRepository' zostanie zapytany o email 'dr.house@test.pl'..."
        when(userRepository.findByEmail("dr.house@test.pl"))
                .thenReturn(Optional.of(doctorUser)); // "...WTEY zwróć naszego fałszywego lekarza"

        // 2. "KIEDY 'doctorRepository' zostanie zapytany o obiekt 'doctorUser'..."
        when(doctorRepository.findByUser(doctorUser))
                .thenReturn(Optional.of(doctor)); // "...WTEY zwróć fałszywy profil lekarza"

        // --- ACT (Działanie) ---
        // Uruchamiamy metodę, którą testujemy
        slotService.createSlot(slotDto, "dr.house@test.pl");

        // --- ASSERT (Sprawdzenie) ---
        // Chcemy sprawdzić, czy na końcu został wywołany zapis do bazy.
        // Sprawdź (verify), czy na 'availableSlotRepository' została wywołana metoda 'save'
        // DOKŁADNIE 1 raz, z DOWOLNYM (any) obiektem typu AvailableSlot.
        verify(availableSlotRepository, times(1)).save(any(AvailableSlot.class));
    }

    @Test
    void createSlot_ShouldThrowException_WhenUserNotFound() {
        // --- ARRANGE (Ustawienia) ---
        // 1. "KIEDY 'userRepository' zostanie zapytany o nieistniejący email..."
        when(userRepository.findByEmail("nieistnieje@test.pl"))
                .thenReturn(Optional.empty()); // "...WTEY zwróć 'pusto'"

        // --- ACT & ASSERT (Działanie i Sprawdzenie) ---
        // Sprawdzamy, czy uruchomienie metody rzuci oczekiwanym błędem
        assertThrows(UsernameNotFoundException.class, () -> {
            // Uruchamiamy metodę z tym nieistniejącym emailem
            slotService.createSlot(slotDto, "nieistnieje@test.pl");
        });

        // Dodatkowo sprawdźmy, czy *nigdy* nie próbowaliśmy niczego zapisywać
        verify(availableSlotRepository, never()).save(any(AvailableSlot.class));
    }

    @Test
    void createSlot_ShouldThrowException_WhenUserIsNotDoctor() {
        // --- ARRANGE (Ustawienia) ---
        // 1. "KIEDY 'userRepository' zostanie zapytany o email pacjenta..."
        when(userRepository.findByEmail("anna.nowak@test.pl"))
                .thenReturn(Optional.of(patientUser)); // "...WTEY zwróć naszego fałszywego pacjenta"

        // 2. "KIEDY 'doctorRepository' zostanie zapytany o obiekt 'patientUser'..."
        when(doctorRepository.findByUser(patientUser))
                .thenReturn(Optional.empty()); // "...WTEY zwróć 'pusto' (bo pacjent nie ma profilu lekarza)"

        // --- ACT & ASSERT (Działanie i Sprawdzenie) ---
        // Sprawdzamy, czy rzuci błędem 'RuntimeException' (tak zdefiniowaliśmy w serwisie)
        assertThrows(RuntimeException.class, () -> {
            slotService.createSlot(slotDto, "anna.nowak@test.pl");
        });

        // I upewnijmy się, że komunikat błędu też się zgadza
        Exception exception = assertThrows(RuntimeException.class, () -> {
            slotService.createSlot(slotDto, "anna.nowak@test.pl");
        });
        assertEquals("User is not a doctor", exception.getMessage());

        // I oczywiście, nic nie zostało zapisane
        verify(availableSlotRepository, never()).save(any(AvailableSlot.class));
    }
}