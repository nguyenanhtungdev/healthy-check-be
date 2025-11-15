package org.tung.healthycheck.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.tung.healthycheck.dto.ChatSessionDTO;
import org.tung.healthycheck.model.Account;
import org.tung.healthycheck.model.ChatMessage;
import org.tung.healthycheck.model.ChatSession;
import org.tung.healthycheck.services.AuthService;
import org.tung.healthycheck.services.ChatHistoryService;
import org.tung.healthycheck.services.GeminiAIService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIRestController {
    private final GeminiAIService aiService;
    private final AuthService authService;
    private final ChatHistoryService historyService;

    @PostMapping("/session")
    public Map<String, Object> createSession() {
        Account acc = authService.getCurrentAccount();
        ChatSession session = aiService.createSession(acc);

        return Map.of(
                "session_id", session.getId().toString()
        );
    }

    @GetMapping("/history/{sessionId}")
    public ChatSessionDTO getSession(@PathVariable UUID sessionId) {
        return historyService.getSessionDTO(sessionId);
    }

    @PostMapping("/ask/{sessionId}")
    public Map<String, Object> ask(
            @PathVariable UUID sessionId,
            @RequestBody Map<String, String> body) {

        String prompt = body.get("prompt");
        String response = aiService.ask(sessionId, prompt);

        return Map.of(
                "sessionId", sessionId,
                "prompt", prompt,
                "response", response
        );
    }
}
