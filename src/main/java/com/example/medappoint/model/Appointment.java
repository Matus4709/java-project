package com.example.medappoint.model;

import com.example.medappoint.model.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Wiele wizyt może należeć do jednego pacjenta
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    // Wiele wizyt może należeć do jednego lekarza
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    // Jedna wizyta jest przypisana do jednego slotu
    @OneToOne
    @JoinColumn(name = "slot_id", referencedColumnName = "id", unique = true)
    private AvailableSlot availableSlot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    private String notes; // Notatki z wizyty (np. od lekarza)
}