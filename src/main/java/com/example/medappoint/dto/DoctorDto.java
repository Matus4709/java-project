
/// To jest nasze tak zwane gotowe danie.

package com.example.medappoint.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class DoctorDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String specialization;
    private String description;
}
