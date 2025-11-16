package com.example.medappoint.repository;

import com.example.medappoint.model.Doctor;
import com.example.medappoint.model.Patient;
import com.example.medappoint.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    Optional<Patient> findByUser(User user);
}