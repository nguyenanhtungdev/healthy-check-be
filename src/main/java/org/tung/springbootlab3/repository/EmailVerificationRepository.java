package org.tung.springbootlab3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tung.springbootlab3.model.EmailVerification;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, UUID> {
    Optional<EmailVerification> findByEmailAndCode(String email, String code);
    Optional<EmailVerification> findTopByEmailOrderByCreatedAtDesc(String email);
}