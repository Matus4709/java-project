package com.example.medappoint;

// WAŻNE NOWE IMPORTY
import com.example.medappoint.dto.PatientDto;
import com.example.medappoint.dto.PatientRegistrationDto;
import com.example.medappoint.exception.EmailAlreadyExistsException;
import com.example.medappoint.service.PatientService;
import org.springframework.boot.CommandLineRunner;
// --------------------

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean; // Potrzebne do @Bean

@SpringBootApplication
public class MedappointApplication { // Nie musisz już pisać "implements CommandLineRunner"

    public static void main(String[] args) {
        SpringApplication.run(MedappointApplication.class, args);
    }

    // Ta adnotacja @Bean mówi Springowi, żeby uruchomił tę metodę
    // To jest czystszy sposób niż implementowanie interfejsu w głównej klasie
    @Bean
    public CommandLineRunner testPatientService(PatientService patientService) {
        return args -> {
            System.out.println("\n--- URUCHAMIAM TEST SERWISU REJESTRACJI ---");

            // 1. Przygotuj fałszywe dane rejestracyjne
            PatientRegistrationDto testDto = new PatientRegistrationDto();
            testDto.setEmail("test@test.pl");
            testDto.setPassword("haslo123");
            testDto.setFirstName("Jan");
            testDto.setLastName("Kowalski");
            testDto.setPesel("90010112345");
            testDto.setPhoneNumber("500100200");

            // 2. Użyj bloku try-catch, aby przetestować oba scenariusze
            try {
                PatientDto zarejestrowanyPacjent = patientService.registerPatient(testDto);
                System.out.println("SUKCES! Zarejestrowano pacjenta: " + zarejestrowanyPacjent);

            } catch (EmailAlreadyExistsException e) {
                System.err.println("TEST ZAKOŃCZONY PRAWIDŁOWYM BŁĘDEM: " + e.getMessage());

            } catch (Exception e) {
                System.err.println("WYSTĄPIŁ NIEOCZEKIWANY BŁĄD: " + e.getMessage());
            }

            System.out.println("--- TEST ZAKOŃCZONY ---\n");
        };
    }
}