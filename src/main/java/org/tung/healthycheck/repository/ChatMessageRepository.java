package org.tung.healthycheck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tung.healthycheck.model.ChatMessage;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    List<ChatMessage> findBySession_IdOrderByCreatedAtAsc(UUID sessionId);
}
