package org.tung.healthycheck.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.tung.healthycheck.dto.AppointmentResponseDTO;
import org.tung.healthycheck.model.Account;
import org.tung.healthycheck.model.AppointmentSchedule;
import org.tung.healthycheck.model.User;
import org.tung.healthycheck.services.AccountService;
import org.tung.healthycheck.services.AppointmentScheduleService;
import org.tung.healthycheck.services.AuthService;
import org.tung.healthycheck.services.UserService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/appointments")
public class AppointmentScheduleController {
    @Autowired
    private AppointmentScheduleService appointmentScheduleService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;

    @PostMapping("/create")
    public ResponseEntity<Map<String,Object>> createAppointment(@RequestBody Map<String, Object> body) {
        User creator = authService.getCurrentUser();

        String hospital = (String) body.get("hospital");
        String frequency = (String) body.get("frequency");
        String note = (String) body.get("note");
        LocalDate firstDate = LocalDate.parse((String) body.get("firstDate"));
        List<String> memberIdsStr = (List<String>) body.get("memberIds");
        List<UUID> memberIds = memberIdsStr.stream().map(UUID::fromString).toList();

        AppointmentSchedule schedule = appointmentScheduleService.createSchedule(
                creator.getId(), hospital, frequency, firstDate, note, memberIds
        );

        Map<String, Object> response = new HashMap<>();
        response.put("id", schedule.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointments() {
        User creator = authService.getCurrentUser();
        List<AppointmentResponseDTO> response = appointmentScheduleService.getSchedulesByUserDTO(creator.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String,Object>> updateAppointment(@PathVariable UUID id, @RequestBody Map<String, Object> body) {
        String hospital = (String) body.get("hospital");
        String frequency = (String) body.get("frequency");
        String note = (String) body.get("note");
        LocalDate firstDate = LocalDate.parse((String) body.get("firstDate"));
        List<String> memberIdsStr = (List<String>) body.get("memberIds");
        List<UUID> memberIds = memberIdsStr.stream().map(UUID::fromString).toList();
        Map<String, Object> response = new HashMap<>();
        response.put("id", appointmentScheduleService.updateSchedule(id, hospital, frequency, firstDate, note, memberIds).getId());
        return ResponseEntity.ok(
                response
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable UUID id) {
        appointmentScheduleService.deleteSchedule(id);
        return ResponseEntity.ok(Map.of("message", "Xóa lịch khám thành công"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointmentDetail(@PathVariable UUID id) {
        try {
            User currentUser = authService.getCurrentUser();
            AppointmentResponseDTO dto = appointmentScheduleService.getScheduleDetail(id, currentUser.getId());
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }
}
