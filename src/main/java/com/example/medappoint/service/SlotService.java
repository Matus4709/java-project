package com.example.medappoint.service;


import com.example.medappoint.dto.SlotCreateDto;
import com.example.medappoint.model.AvailableSlot;
import com.example.medappoint.model.Doctor;
import com.example.medappoint.model.User;
import com.example.medappoint.repository.AvailableSlotRepository;
import com.example.medappoint.repository.DoctorRepository;
import com.example.medappoint.repository.PatientRepository;
import com.example.medappoint.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SlotService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final AvailableSlotRepository availableSlotRepository;

    @Transactional
    public void createSlot(SlotCreateDto slotDto, String doctorEmail){
        User user = userRepository.findByEmail(doctorEmail)
                .orElseThrow( () -> new UsernameNotFoundException("User not found with email: " + doctorEmail));

        Doctor doctor = doctorRepository.findByUser(user)
                .orElseThrow( () -> new UsernameNotFoundException("User is not a doctor"));

    AvailableSlot newSlot = AvailableSlot.builder()
            .startTime(slotDto.getStartTime())
            .endTime(slotDto.getEndTime())
            .isBooked(false)
            .doctor(doctor)
            .build();

    availableSlotRepository.save(newSlot);
    }
}
