package org.tung.healthycheck.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tung.healthycheck.dto.ChatMessageDTO;
import org.tung.healthycheck.dto.ChatSessionDTO;
import org.tung.healthycheck.model.ChatSession;
import org.tung.healthycheck.repository.ChatMessageRepository;
import org.tung.healthycheck.repository.ChatSessionRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatHistoryService {

    private final ChatSessionRepository sessionRepo;
    private final ChatMessageRepository messageRepo;

    public ChatSessionDTO getSessionDTO(UUID sessionId) {

        ChatSession s = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        List<ChatMessageDTO> msgs = messageRepo
                .findBySession_IdOrderByCreatedAtAsc(sessionId)
                .stream()
                .map(m -> new ChatMessageDTO(
                        m.getId(),
                        m.getSender(),
                        m.getMessage(),
                        m.getCreatedAt()
                ))
                .toList();

        return new ChatSessionDTO(
                s.getId(),
                s.getCreatedAt(),
                s.getUpdatedAt(),
                msgs
        );
    }
}
