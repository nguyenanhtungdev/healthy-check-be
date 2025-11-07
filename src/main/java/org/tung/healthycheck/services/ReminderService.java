package org.tung.healthycheck.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.tung.healthycheck.dto.UpdateReminderDTO;
import org.tung.healthycheck.model.Notification;
import org.tung.healthycheck.model.Reminder;
import org.tung.healthycheck.model.User;
import org.tung.healthycheck.repository.NotificationRepository;
import org.tung.healthycheck.repository.ReminderRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReminderService {

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    // Tạo nhắc nhở mới
    public Reminder createReminder(User user, String title, String note, String category, LocalDateTime remindAt) {
        Reminder reminder = new Reminder();
        reminder.setUser(user);
        reminder.setTitle(title);
        reminder.setNote(note);
        reminder.setCategory(category);
        reminder.setRemindAt(remindAt);
        reminder.setSent(false);
        return reminderRepository.save(reminder);
    }

    // Lấy danh sách nhắc nhở của người dùng
    public List<Reminder> getReminders(UUID userId) {
        return reminderRepository.findByUser_Id(userId);
    }

    //Job chạy mỗi phút để kiểm tra nhắc nhở đến hạn
    @Scheduled(fixedRate = 60000) // 60000 ms = 1 phút
    public void checkAndSendReminders() {
        LocalDateTime now = LocalDateTime.now();
        List<Reminder> dueReminders = reminderRepository.findDueReminders(now);

        for (Reminder reminder : dueReminders) {
            try {
                User user = reminder.getUser();

                Notification noti = new Notification();
                noti.setUser(user);
                noti.setTitle(reminder.getTitle());
                noti.setContent(reminder.getNote() != null ? reminder.getNote() : "Đến giờ nhắc nhở!");
                noti.setType("nhac_nho");
                noti.setCreatedAt(LocalDateTime.now());
                noti.setIsRead(false);
                noti.setReminder(reminder);
                notificationRepository.save(noti);

                reminder.setSent(true);
                reminderRepository.save(reminder);

                System.out.println("Đã gửi nhắc nhở: " + reminder.getTitle() + " cho user " + user.getUsername());
            } catch (Exception e) {
                System.out.println("Lỗi khi gửi nhắc nhở: " + e.getMessage());
            }
        }
    }

    public Reminder updateReminder(UUID userId, UUID reminderId, UpdateReminderDTO dto) {
        Reminder r = reminderRepository.findByIdAndUser_Id(reminderId, userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhắc nhở"));

        if (Boolean.TRUE.equals(r.getSent())) {
            // đã gửi -> không cho sửa
            throw new IllegalStateException("Nhắc nhở đã được gửi, không thể sửa");
        }

        if (dto.getTitle() != null)    r.setTitle(dto.getTitle());
        if (dto.getNote() != null)     r.setNote(dto.getNote());
        if (dto.getCategory() != null) r.setCategory(dto.getCategory());
        if (dto.getRemindAt() != null) r.setRemindAt(dto.getRemindAt());

        return reminderRepository.save(r);
    }

    public void deleteReminder(UUID userId, UUID reminderId) {
        Reminder r = reminderRepository.findByIdAndUser_Id(reminderId, userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhắc nhở"));

        if (Boolean.TRUE.equals(r.getSent())) {
            throw new IllegalStateException("Nhắc nhở đã được gửi, không thể xóa");
        }

        reminderRepository.delete(r);
    }

    public Reminder getReminderById(UUID userId, UUID reminderId) {
        return reminderRepository.findByIdAndUser_Id(reminderId, userId)
                .orElse(null);
    }
}
