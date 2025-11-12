package com.example.medappoint.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {
    @GetMapping
    public ResponseEntity<List<String>> getAllDoctors() {
        List<String> fakeDoctors = List.of("Dr. Jan Kowalski (Kardiolog)", "Dr. Anna Nowak (Pediatra)");

        return ResponseEntity.ok().body(fakeDoctors);
    }
}
