package com.example.medappoint.service;

import com.example.medappoint.dto.AppointmentDto;
import com.example.medappoint.model.*;
import com.example.medappoint.model.enums.AppointmentStatus;
import com.example.medappoint.repository.AppointmentRepository;
import com.example.medappoint.repository.AvailableSlotRepository;
import com.example.medappoint.repository.PatientRepository;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    // --- 1. Konfiguracja ---

    // Udajemy wszystkich naszych "Kierowników Magazynu"
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private AvailableSlotRepository availableSlotRepository;
    @Mock
    private UserRepository userRepository;

    // To jest nasz "Mistrz Ceremonii" (Kucharz), którego testujemy
    @InjectMocks
    private AppointmentService appointmentService;

    // Zmienne pomocnicze dla naszych "fałszywych" danych
    private User patientUser;
    private Patient patient;
    private Doctor doctor;
    private AvailableSlot availableSlot;
    private Appointment savedAppointment;
    private UUID slotId;
    private String patientEmail;

    // Przygotuj świeże dane przed każdym testem
    @BeforeEach
    void setUp() {
        slotId = UUID.randomUUID();
        patientEmail = "anna.nowak@test.pl";

        // Stwórz fałszywego Użytkownika-Pacjenta
        patientUser = new User();
        patientUser.setEmail(patientEmail);

        // Stwórz fałszywy Profil Pacjenta
        patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setUser(patientUser);
        patient.setFirstName("Anna");
        patient.setLastName("Nowak");

        // Stwórz fałszywy Profil Lekarza
        doctor = new Doctor();
        doctor.setId(UUID.randomUUID());
        doctor.setFirstName("Gregory");
        doctor.setLastName("House");
        doctor.setSpecialization("Diagnosta");

        // Stwórz fałszywy, DOSTĘPNY slot
        availableSlot = new AvailableSlot();
        availableSlot.setId(slotId);
        availableSlot.setBooked(false); // Kluczowe: Slot jest wolny
        availableSlot.setDoctor(doctor); // Slot należy do Dr. House'a
        availableSlot.setStartTime(LocalDateTime.now().plusDays(1));
        availableSlot.setEndTime(LocalDateTime.now().plusDays(1).plusHours(1));

        // Stwórz fałszywą, zapisaną Wizytę (to jest to, co zwróci .save())
        savedAppointment = Appointment.builder()
                .id(UUID.randomUUID())
                .status(AppointmentStatus.SCHEDULED)
                .patient(patient)
                .doctor(doctor)
                .availableSlot(availableSlot)
                .build();
    }

    // --- 2. Testy ---

    @Test
    void bookAppointment_ShouldSucceed_WhenSlotIsAvailableAndPatientExists() {
        // --- ARRANGE (Ustawienia) ---
        // Ustaw zachowanie mocków dla scenariusza pomyślnego

        // 1. "KIEDY 'userRepository' jest pytany o email pacjenta..."
        when(userRepository.findByEmail(patientEmail)).thenReturn(Optional.of(patientUser));

        // 2. "KIEDY 'patientRepository' jest pytany o obiekt 'patientUser'..."
        when(patientRepository.findByUser(patientUser)).thenReturn(Optional.of(patient));

        // 3. "KIEDY 'availableSlotRepository' jest pytany o 'slotId'..."
        when(availableSlotRepository.findById(slotId)).thenReturn(Optional.of(availableSlot));

        // 4. "KIEDY 'appointmentRepository' jest proszony o zapis DOWOLNEJ wizyty..."
        // Musimy upewnić się, że mock .save() zwraca obiekt, aby nasz kod mógł go zmapować
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        // --- ACT (Działanie) ---
        // Uruchom metodę, którą testujemy
        AppointmentDto resultDto = appointmentService.bookAppointment(slotId, patientEmail);

        // --- ASSERT (Sprawdzenie) ---
        // Sprawdź, czy zwrócony "Paragon" (DTO) zawiera poprawne dane
        assertNotNull(resultDto);
        assertEquals(AppointmentStatus.SCHEDULED, resultDto.getStatus());
        assertEquals("Anna", resultDto.getPatientFirstName());
        assertEquals("Gregory", resultDto.getDoctorFirstName());
        assertEquals(availableSlot.getStartTime(), resultDto.getStartTime());

        // Kluczowe: Sprawdź, czy slot został oznaczony jako ZAJĘTY
        assertTrue(availableSlot.isBooked());

        // Sprawdź, czy oba zapisy do bazy zostały wywołane DOKŁADNIE 1 raz
        verify(availableSlotRepository, times(1)).save(availableSlot);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void bookAppointment_ShouldThrowException_WhenSlotIsAlreadyBooked() {
        // --- ARRANGE (Ustawienia) ---
        // Zmieńmy jeden warunek: slot jest już ZAJĘTY
        availableSlot.setBooked(true);

        // Ustaw mocki (musimy nadal znaleźć pacjenta i slot)
        when(userRepository.findByEmail(patientEmail)).thenReturn(Optional.of(patientUser));
        when(patientRepository.findByUser(patientUser)).thenReturn(Optional.of(patient));
        when(availableSlotRepository.findById(slotId)).thenReturn(Optional.of(availableSlot));

        // --- ACT & ASSERT (Działanie i Sprawdzenie) ---
        // Sprawdź, czy rzucony zostanie wyjątek
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            appointmentService.bookAppointment(slotId, patientEmail);
        });
        assertTrue(ex.getMessage().contains("already booked"));

        // Co najważniejsze: sprawdź, czy NIGDY nie próbowaliśmy nic zapisać!
        verify(appointmentRepository, never()).save(any(Appointment.class));
        verify(availableSlotRepository, never()).save(any(AvailableSlot.class));
    }

    @Test
    void bookAppointment_ShouldThrowException_WhenPatientProfileNotFound() {
        // --- ARRANGE (Ustawienia) ---
        // Znajdziemy Usera, ale nie znajdziemy powiązanego z nim profilu Pacjenta
        when(userRepository.findByEmail(patientEmail)).thenReturn(Optional.of(patientUser));
        when(patientRepository.findByUser(patientUser)).thenReturn(Optional.empty());

        // --- ACT & ASSERT (Działanie i Sprawdzenie) ---
        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class, () -> {
            appointmentService.bookAppointment(slotId, patientEmail);
        });
        assertEquals("Patient not found with this email.", ex.getMessage());

        // Sprawdź, czy NIGDY nie próbowaliśmy nic zapisać!
        verify(appointmentRepository, never()).save(any(Appointment.class));
        verify(availableSlotRepository, never()).save(any(AvailableSlot.class));
    }

    @Test
    void bookAppointment_ShouldThrowException_WhenSlotNotFound() {
        // --- ARRANGE (Ustawienia) ---
        // Znajdziemy pacjenta, ale nie znajdziemy slotu
        when(userRepository.findByEmail(patientEmail)).thenReturn(Optional.of(patientUser));
        when(patientRepository.findByUser(patientUser)).thenReturn(Optional.of(patient));

        // "KIEDY repozytorium jest pytane o 'slotId', zwróć 'pusto'"
        when(availableSlotRepository.findById(slotId)).thenReturn(Optional.empty());

        // --- ACT & ASSERT (Działanie i Sprawdzenie) ---
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            appointmentService.bookAppointment(slotId, patientEmail);
        });
        assertTrue(ex.getMessage().contains("Slot not found"));

        // Sprawdź, czy NIGDY nie próbowaliśmy nic zapisać!
        verify(appointmentRepository, never()).save(any(Appointment.class));
        verify(availableSlotRepository, never()).save(any(AvailableSlot.class));
    }

    @Test
    void bookAppointment_ShouldThrowException_WhenUserNotFound() {
        // --- ARRANGE (Ustawienia) ---
        // Nie znajdziemy użytkownika
        when(userRepository.findByEmail(patientEmail)).thenReturn(Optional.empty());

        // --- ACT & ASSERT (Działanie i Sprawdzenie) ---
        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class, () -> {
            appointmentService.bookAppointment(slotId, patientEmail);
        });
        assertEquals("Username not found with this email.", ex.getMessage());

        // Sprawdź, czy NIGDY nie próbowaliśmy nic zapisać!
        verify(appointmentRepository, never()).save(any(Appointment.class));
        verify(availableSlotRepository, never()).save(any(AvailableSlot.class));
        verify(patientRepository, never()).findByUser(any(User.class));
    }
}