package com.example.medappoint.repository;

import com.example.medappoint.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    // Metody do wyszukiwania wizyt pacjenta, lekarza itp.
}