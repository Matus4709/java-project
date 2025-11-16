package com.example.medappoint.service;

import com.example.medappoint.dto.AppointmentDto;
import com.example.medappoint.model.*;
import com.example.medappoint.model.enums.AppointmentStatus;
import com.example.medappoint.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AvailableSlotRepository availableSlotRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional
    public AppointmentDto bookAppointment(UUID slotId, String patientEmail) {
        // Znajdowanie pacjenta
        User user = userRepository.findByEmail(patientEmail)
                .orElseThrow( () -> new UsernameNotFoundException("Username not found with this email."));
        Patient patient = patientRepository.findByUser(user)
                .orElseThrow( () -> new UsernameNotFoundException("Patient not found with this email."));
        // Znajdowanie slota po id
        AvailableSlot slot = availableSlotRepository.findById(slotId)
                .orElseThrow( () -> new RuntimeException("Slot not found with id: ."+slotId));
        if (slot.isBooked()){
            throw new RuntimeException("This slot (ID: \" + slotId + \") is already booked.");
        }
        slot.setBooked(true);
        availableSlotRepository.save(slot);

        //Tworzenie wizyty
        Appointment newAppointment = Appointment.builder()
                .status(AppointmentStatus.SCHEDULED)
                .patient(patient)
                .doctor(slot.getDoctor())
                .availableSlot(slot)
                .build();

        // Zapis nowej wizyty w bazie
        Appointment savedAppointment = appointmentRepository.save(newAppointment);

        // Zwróć potwierdzenie
        return mapToAppointmentDto(savedAppointment);

    }

    private AppointmentDto mapToAppointmentDto (Appointment appointment){

        Patient patient = appointment.getPatient();
        Doctor doctor = appointment.getDoctor();
        AvailableSlot slot = appointment.getAvailableSlot();

        return AppointmentDto.builder()
                .id(appointment.getId())
                .status(appointment.getStatus())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .patientFirstName(patient.getFirstName())
                .patientLastName(patient.getLastName())
                .doctorFirstName(doctor.getFirstName())
                .doctorLastName(doctor.getLastName())
                .doctorSpecialization(doctor.getSpecialization())
                .build();
    }
}
