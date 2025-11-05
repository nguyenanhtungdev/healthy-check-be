package org.tung.healthycheck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tung.healthycheck.model.Setting;

import java.util.UUID;

@Repository
public interface SettingRepository extends JpaRepository<Setting, UUID> {
    Setting findFirstByOrderByCreatedAtDesc();
}
