package org.tung.healthycheck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.tung.healthycheck.model.AppointmentSchedule;

import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentScheduleRepository extends JpaRepository<AppointmentSchedule, UUID> {
    // Lịch do người dùng tạo
    List<AppointmentSchedule> findByCreatedBy_Id(UUID creatorId);

    // Lịch mà người dùng được thêm vào (trong participants)
    List<AppointmentSchedule> findByParticipants_Id(UUID participantId);

    // Lấy tất cả (do tạo hoặc tham gia)
    @Query("SELECT DISTINCT a FROM AppointmentSchedule a " +
            "LEFT JOIN a.participants p " +
            "WHERE a.createdBy.id = :userId OR p.id = :userId")
    List<AppointmentSchedule> findAllByUserInvolved(UUID userId);
}
