package com.example.medappoint.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class PatientDto {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String pesel;
    private String phoneNumber;
    private String role;
}
