package org.tung.healthycheck.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tung.healthycheck.dto.AppointmentResponseDTO;
import org.tung.healthycheck.dto.ParticipantDTO;
import org.tung.healthycheck.model.AppointmentSchedule;
import org.tung.healthycheck.model.User;
import org.tung.healthycheck.repository.AppointmentScheduleRepository;
import org.tung.healthycheck.repository.UserRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppointmentScheduleService {

    @Autowired
    private AppointmentScheduleRepository appointmentScheduleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationService notificationService;

    public AppointmentSchedule createSchedule(UUID creatorId, String hospital, String frequency,
                                              LocalDate firstDate, String note, List<UUID> memberIds) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người tạo lịch"));

        Set<User> participants = new HashSet<>(userRepository.findAllById(memberIds));

        AppointmentSchedule schedule = new AppointmentSchedule();
        schedule.setHospitalName(hospital);
        schedule.setFrequency(frequency);
        schedule.setFirstDate(firstDate);
        schedule.setNote(note);
        schedule.setCreatedBy(creator);
        schedule.setParticipants(participants);

        AppointmentSchedule saved = appointmentScheduleRepository.save(schedule);

        notificationService.createNotificationsForAppointment(creator, participants, hospital, saved);
        return saved;
    }


    public List<AppointmentSchedule> getSchedulesByUser(UUID userId) {
        return appointmentScheduleRepository.findByCreatedBy_Id(userId);
    }

    public List<AppointmentResponseDTO> getSchedulesByUserDTO(UUID userId) {
        List<AppointmentSchedule> schedules = appointmentScheduleRepository.findAllByUserInvolved(userId);

        return schedules.stream().map(schedule -> {
            List<ParticipantDTO> participantDTOs = schedule.getParticipants().stream()
                    .map(p -> new ParticipantDTO(
                            p.getId(),
                            p.getAccount() != null ? p.getAccount().getImage() : null,
                            p.getFullName(),
                            p.getEmail()
                    ))
                    .toList();

            return new AppointmentResponseDTO(
                    schedule.getId(),
                    schedule.getHospitalName(),
                    schedule.getFrequency(),
                    schedule.getFirstDate(),
                    schedule.getNote(),
                    schedule.getCreatedBy() != null ? schedule.getCreatedBy().getId() : null,
                    participantDTOs
            );
        }).toList();
    }

    public AppointmentSchedule updateSchedule(UUID scheduleId, String hospital, String frequency,
                                              LocalDate firstDate, String note, List<UUID> memberIds) {
        AppointmentSchedule schedule = appointmentScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch khám"));

        schedule.setHospitalName(hospital);
        schedule.setFrequency(frequency);
        schedule.setFirstDate(firstDate);
        schedule.setNote(note);
        schedule.setParticipants(new HashSet<>(userRepository.findAllById(memberIds)));

        AppointmentSchedule saved = appointmentScheduleRepository.save(schedule);
        notificationService.createNotificationsForAppointmentUpdate(saved.getCreatedBy(),
                saved.getParticipants(), hospital, saved);

        return saved;
    }

    public void deleteSchedule(UUID scheduleId) {
        AppointmentSchedule schedule = appointmentScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch khám"));
        Set<User> participants = schedule.getParticipants();
        User deleter = schedule.getCreatedBy();
        String hospitalName = schedule.getHospitalName();

        appointmentScheduleRepository.delete(schedule);
        notificationService.createNotificationsForAppointmentDelete(deleter, participants, hospitalName, schedule);
    }

    public AppointmentResponseDTO getScheduleDetail(UUID appointmentId, UUID userId) {
        AppointmentSchedule schedule = appointmentScheduleRepository
                .findAccessibleByIdAndUser(appointmentId, userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hoặc bạn không có quyền xem"));

        List<ParticipantDTO> participantDTOs = schedule.getParticipants().stream()
                .map(p -> new ParticipantDTO(
                        p.getId(),
                        p.getAccount() != null ? p.getAccount().getImage() : null,
                        p.getFullName(),
                        p.getEmail()
                ))
                .toList();

        return new AppointmentResponseDTO(
                schedule.getId(),
                schedule.getHospitalName(),
                schedule.getFrequency(),
                schedule.getFirstDate(),
                schedule.getNote(),
                schedule.getCreatedBy() != null ? schedule.getCreatedBy().getId() : null,
                participantDTOs
        );
    }

}
