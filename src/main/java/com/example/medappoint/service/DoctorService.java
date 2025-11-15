package com.example.medappoint.service;

import com.example.medappoint.dto.DoctorDto;
import com.example.medappoint.model.Doctor;
import com.example.medappoint.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public List<DoctorDto> findAllDoctors() {

        List<Doctor> doctorsFromDb = doctorRepository.findAll(); // Pobieram z bazy - czyli repo

        return doctorsFromDb.stream()
                .map(this::mapToDoctorDto)
                .toList(); // lub .collect(Collectors.toList())

    }
    private DoctorDto mapToDoctorDto(Doctor doctor){ // Metoda do tłumaczenia
        return DoctorDto.builder()
                .id(doctor.getId())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .specialization(doctor.getSpecialization())
                .description(doctor.getDescription())
                .build();
    }
}
