package org.tung.springbootlab3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tung.springbootlab3.model.User;
import org.tung.springbootlab3.model.UserHealth;

import java.util.Optional;
import java.util.UUID;

public interface UserHealthRepository extends JpaRepository<UserHealth, UUID> {
    Optional<UserHealth> findByUser(User user);
}