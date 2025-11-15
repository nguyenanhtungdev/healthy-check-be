package org.tung.healthycheck.services;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.tung.healthycheck.model.Account;
import org.tung.healthycheck.model.ChatMessage;
import org.tung.healthycheck.model.ChatSession;
import org.tung.healthycheck.repository.ChatMessageRepository;
import org.tung.healthycheck.repository.ChatSessionRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GeminiAIService {

    private final ChatClient chatClient;
    private final ChatSessionRepository sessionRepo;
    private final ChatMessageRepository messageRepo;


    public ChatSession createSession(Account acc) {
        ChatSession s = new ChatSession();
        s.setAccount(acc);
        return sessionRepo.save(s);
    }

    public String ask(UUID sessionId, String prompt) {

        ChatSession session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        // Lưu message của user
        ChatMessage userMsg = new ChatMessage();
        userMsg.setSession(session);
        userMsg.setSender("user");
        userMsg.setMessage(prompt);
        messageRepo.save(userMsg);

        // Tạo prompt hệ thống
        String systemInstruction = """
            Bạn là trợ lý sức khỏe & thể thao.
            Trả lời chi tiết, rõ ràng, dễ hiểu.
            Nếu có JSON (vd: familyMembers), hãy xử lý từng phần tử.
        """;

        String fullPrompt = """
        %s

        Người dùng hỏi:
        %s
        """.formatted(systemInstruction, prompt);

        String aiResponse = chatClient
                .prompt(fullPrompt)
                .call()
                .content();

        // Lưu message AI
        ChatMessage aiMsg = new ChatMessage();
        aiMsg.setSession(session);
        aiMsg.setSender("ai");
        aiMsg.setMessage(aiResponse);
        messageRepo.save(aiMsg);

        return aiResponse;
    }
}
