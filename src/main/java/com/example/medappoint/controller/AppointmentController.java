package com.example.medappoint.controller;

import com.example.medappoint.dto.AppointmentDto;
import com.example.medappoint.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/{slotId}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentDto> bookAppointment(
            @PathVariable UUID slotId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // Adres email zalogowanego użytkownika
        String patientEmail = userDetails.getUsername();

        AppointmentDto createdAppointmentDto = appointmentService.bookAppointment(slotId, patientEmail);

        return new ResponseEntity<>(createdAppointmentDto, HttpStatus.CREATED);
    }
}
