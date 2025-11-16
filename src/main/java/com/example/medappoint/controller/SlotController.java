package com.example.medappoint.controller;

import com.example.medappoint.dto.SlotCreateDto;
import com.example.medappoint.service.SlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.SessionStatus;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/slots")
public class SlotController {

    private final SlotService slotService;

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Void> createSlot(@Valid @RequestBody SlotCreateDto slotCreateDto,
                                           @AuthenticationPrincipal UserDetails userDetails, SessionStatus sessionStatus) {
        String email =  userDetails.getUsername();
        slotService.createSlot(slotCreateDto, email);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
