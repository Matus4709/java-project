package com.example.medappoint.controller;

import com.example.medappoint.dto.AuthRequestDto;
import com.example.medappoint.dto.AuthResponseDto;
import com.example.medappoint.dto.PatientDto;
import com.example.medappoint.dto.PatientRegistrationDto;
import com.example.medappoint.service.AuthService;
import com.example.medappoint.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// @RestController: Mówi Springowi, że ta klasa to "Kelner" (Kontroler),
// który będzie automatycznie zamieniał obiekty Javy na JSON.
@RestController

// @RequestMapping: Ustawia bazowy adres dla wszystkich metod w tej klasie.
// Wszystkie "drzwi" tutaj będą zaczynać się od "/api/auth"
@RequestMapping("/api/auth")

// @RequiredArgsConstructor: Tworzy konstruktor dla pól "final" (PatientService i AuthService).
// To jest sposób, w jaki Spring "wstrzykuje" nam nasze serwisy.
@RequiredArgsConstructor
public class AuthController {

    // Wstrzykujemy naszych "kucharzy"
    private final PatientService patientService;
    private final AuthService authService;

    // --- ENDPOINT REJESTRACJI ---

    // @PostMapping: Mówi Springowi, że ta metoda obsługuje żądania HTTP POST
    // (POST = "Chcę coś STWORZYĆ/WYSŁAĆ")
    // Adres tych drzwi to: [POST] /api/auth/register
    @PostMapping("/register")

    // ResponseEntity<PatientDto>: Mówi, że zwrócimy pełną odpowiedź HTTP (np. status 201)
    // z obiektem PatientDto (w JSON) w środku.

    // @Valid: Mówi Springowi, żeby sprawdził adnotacje z DTO (np. @NotBlank, @Email)

    // @RequestBody: To jest magia. Mówi Springowi: "Weź JSON-a, którego wysłał
    // klient i automatycznie zamień go na obiekt Javy typu PatientRegistrationDto"
    public ResponseEntity<PatientDto> registerPatient(@Valid @RequestBody PatientRegistrationDto registrationDto) {

        // 1. Zleć pracę kucharzowi (serwisowi)
        PatientDto newPatient = patientService.registerPatient(registrationDto);

        // 2. Jeśli się udało, zwróć odpowiedź "201 Created" (to standard REST)
        // To jest lepsze niż "200 OK", bo oznacza "Stworzyłem coś nowego"
        return new ResponseEntity<>(newPatient, HttpStatus.CREATED);
    }

    // --- ENDPOINT LOGOWANIA ---

    // Adres tych drzwi to: [POST] /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto authRequest) {

        // 1. Zleć logowanie serwisowi
        AuthResponseDto response = authService.login(authRequest);

        // 2. Jeśli się udało, zwróć "200 OK" i token w środku
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}