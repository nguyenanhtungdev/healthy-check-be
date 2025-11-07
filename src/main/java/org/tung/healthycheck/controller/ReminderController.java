package org.tung.healthycheck.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tung.healthycheck.dto.ReminderResponseDTO;
import org.tung.healthycheck.dto.UpdateReminderDTO;
import org.tung.healthycheck.model.Reminder;
import org.tung.healthycheck.model.User;
import org.tung.healthycheck.services.AuthService;
import org.tung.healthycheck.services.ReminderService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/reminders")
public class ReminderController {

    @Autowired
    private ReminderService reminderService;

    @Autowired
    private AuthService authService;

    //API tạo nhắc nhở
    @PostMapping
    public ResponseEntity<?> createReminder(@RequestBody Map<String, String> body) {
        User user = authService.getCurrentUser();
        String title = body.get("title");
        String note = body.get("note");
        String category = body.getOrDefault("category", "CHUNG");
        LocalDateTime remindAt = LocalDateTime.parse(body.get("remindAt")); // format ISO: "2025-11-08T09:00:00"

        Reminder r = reminderService.createReminder(user, title, note, category, remindAt);
        return ResponseEntity.ok(Map.of("id", r.getId(), "message", "Tạo nhắc nhở thành công"));
    }

    @GetMapping
    public ResponseEntity<List<ReminderResponseDTO>> getReminders() {
        User user = authService.getCurrentUser();

        // Lấy danh sách Reminder của user
        List<Reminder> reminders = reminderService.getReminders(user.getId());

        // Chuyển sang DTO để tránh lỗi lazy loading
        List<ReminderResponseDTO> dtoList = reminders.stream()
                .map(reminder -> new ReminderResponseDTO(
                        reminder.getId(),
                        reminder.getTitle(),
                        reminder.getNote(),
                        reminder.getCategory(),
                        reminder.getRemindAt(),
                        reminder.getSent()
                ))
                .toList();

        return ResponseEntity.ok(dtoList);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<?> updateReminder(@PathVariable UUID id, @RequestBody UpdateReminderDTO dto) {
        User user = authService.getCurrentUser();
        try {
            Reminder updated = reminderService.updateReminder(user.getId(), id, dto);
            return ResponseEntity.ok(Map.of(
                    "id", updated.getId(),
                    "message", "Cập nhật nhắc nhở thành công"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReminder(@PathVariable UUID id) {
        User user = authService.getCurrentUser();
        try {
            reminderService.deleteReminder(user.getId(), id);
            return ResponseEntity.ok(Map.of("message", "Xóa nhắc nhở thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReminderById(@PathVariable("id") UUID id) {
        User user = authService.getCurrentUser();
        Reminder reminder = reminderService.getReminderById(user.getId(), id);
        if (reminder == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Không tìm thấy nhắc nhở"));
        }

        ReminderResponseDTO dto = new ReminderResponseDTO(
                reminder.getId(),
                reminder.getTitle(),
                reminder.getNote(),
                reminder.getCategory(),
                reminder.getRemindAt(),
                reminder.getSent()
        );
        return ResponseEntity.ok(dto);
    }
}
