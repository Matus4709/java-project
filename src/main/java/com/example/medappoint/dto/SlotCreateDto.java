package com.example.medappoint.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SlotCreateDto {

    @Future(message = "Start time must be in the future")
    @NotNull(message = "Start time cannot be null")
    private LocalDateTime startTime;

    @Future(message = "End time must be in the future")
    @NotNull(message = "End time cannot be null")
    private LocalDateTime endTime;
}
