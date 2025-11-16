package com.example.medappoint.dto;

import com.example.medappoint.model.enums.AppointmentStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class AppointmentDto {
    private UUID id;
    private AppointmentStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String patientFirstName;
    private String patientLastName;
    private String doctorFirstName;
    private String doctorLastName;
    private String doctorSpecialization;
}
