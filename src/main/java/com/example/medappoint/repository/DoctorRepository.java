package com.example.medappoint.repository;

import com.example.medappoint.model.Doctor;
import com.example.medappoint.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    List<Doctor> findBySpecialization(String specialization);
    Optional<Doctor> findByUser(User user);
}