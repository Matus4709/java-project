package com.example.medappoint.repository;

import com.example.medappoint.model.AvailableSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface AvailableSlotRepository extends JpaRepository<AvailableSlot, UUID> {
    // Tutaj w przyszłości dodamy bardziej skomplikowane zapytania
    // np. o wolne sloty danego lekarza w danym dniu
}