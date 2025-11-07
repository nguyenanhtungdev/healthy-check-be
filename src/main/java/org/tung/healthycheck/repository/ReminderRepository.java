package org.tung.healthycheck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.tung.healthycheck.model.Reminder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, UUID> {

    // Lấy danh sách nhắc nhở của user
    List<Reminder> findByUser_Id(UUID userId);

    // Lấy nhắc nhở đã đến hạn mà chưa gửi thông báo
    @Query("SELECT r FROM Reminder r WHERE r.sent = false AND r.remindAt <= :now")
    List<Reminder> findDueReminders(LocalDateTime now);
    Optional<Reminder> findByIdAndUser_Id(UUID id, UUID userId);
}
