package org.tung.healthycheck.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tung.healthycheck.model.Notification;
import org.tung.healthycheck.model.User;
import org.tung.healthycheck.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.tung.healthycheck.dto.NotificationDTO;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    // Lấy danh sách thông báo (DTO)
    public List<NotificationDTO> getUserNotifications(UUID userId) {
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // Lấy thông báo chưa đọc (DTO)
    public List<NotificationDTO> getUnreadNotifications(UUID userId) {
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .filter(n -> !Boolean.TRUE.equals(n.getIsRead()))
                .map(this::convertToDTO)
                .toList();
    }

    public void markAllAsRead(UUID userId) {
        List<Notification> list = notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId);
        list.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(list);
    }

    public void createNotification(User user, String title, String content, String type) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);
        notificationRepository.save(notification);
    }

    public void createNotificationsForAppointment(User creator, Set<User> participants, String hospitalName) {
        // Cho người tạo
        createNotification(
                creator,
                "Tạo lịch khám thành công",
                "Bạn đã tạo lịch khám tại " + hospitalName,
                "lich_kham"
        );

        // Cho từng người được thêm
        for (User p : participants) {
            if (!p.getId().equals(creator.getId())) {
                createNotification(
                        p,
                        "Được thêm vào lịch khám",
                        "Bạn đã được " + creator.getFullName() + " thêm vào lịch khám tại " + hospitalName,
                        "lich_kham"
                );
            }
        }
    }

    // Convert Entity -> DTO
    private NotificationDTO convertToDTO(Notification notification) {
        return new NotificationDTO(
                notification.getId(),
                notification.getTitle(),
                notification.getContent(),
                notification.getType(),
                notification.getIsRead(),
                notification.getCreatedAt()
        );
    }

    public void markAsRead(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông báo"));
        if (!notification.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền cập nhật thông báo này");
        }
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    public void createNotificationsForAppointmentUpdate(User updater, Set<User> participants, String hospitalName) {
        // Cho người cập nhật
        createNotification(
                updater,
                "Cập nhật lịch khám",
                "Bạn đã cập nhật lịch khám tại " + hospitalName,
                "lich_kham"
        );

        // Cho các thành viên khác
        for (User p : participants) {
            if (!p.getId().equals(updater.getId())) {
                createNotification(
                        p,
                        "Lịch khám đã được cập nhật",
                        updater.getFullName() + " đã cập nhật lịch khám tại " + hospitalName,
                        "lich_kham"
                );
            }
        }
    }

    //Khi xóa lịch khám
    public void createNotificationsForAppointmentDelete(User deleter, Set<User> participants, String hospitalName) {
        // Cho người xóa
        createNotification(
                deleter,
                "Xóa lịch khám",
                "Bạn đã xóa lịch khám tại " + hospitalName,
                "lich_kham"
        );

        // Cho các thành viên khác
        for (User p : participants) {
            if (!p.getId().equals(deleter.getId())) {
                createNotification(
                        p,
                        "Lịch khám đã bị hủy",
                        deleter.getFullName() + " đã hủy lịch khám tại " + hospitalName,
                        "lich_kham"
                );
            }
        }
    }
}

