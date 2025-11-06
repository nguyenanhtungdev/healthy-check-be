package org.tung.healthycheck.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.tung.healthycheck.dto.NotificationDTO;
import org.tung.healthycheck.model.Account;
import org.tung.healthycheck.model.User;
import org.tung.healthycheck.services.AccountService;
import org.tung.healthycheck.services.AuthService;
import org.tung.healthycheck.services.NotificationService;
import org.tung.healthycheck.services.UserService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getNotifications() {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(notificationService.getUserNotifications(user.getId()));
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications() {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(notificationService.getUnreadNotifications(user.getId()));
    }

    @PostMapping("/mark-read")
    public ResponseEntity<Map<String, String>> markAllAsRead() {
        User user = authService.getCurrentUser();
        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.ok(Map.of("message", "Đã đánh dấu tất cả là đã đọc"));
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createNotification(@RequestBody Map<String, String> body) {
        User user = authService.getCurrentUser();
        String title = body.get("title");
        String content = body.get("content");
        String type = body.getOrDefault("type", "thong_bao");
        notificationService.createNotification(user, title, content, type);
        return ResponseEntity.ok(Map.of("message", "Đã thêm thông báo mới"));
    }

    @PatchMapping("/{id}/mark-read")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable UUID id) {
        User user = authService.getCurrentUser();
        notificationService.markAsRead(id, user.getId());
        return ResponseEntity.ok(Map.of("message", "Thông báo đã được đánh dấu là đã đọc"));
    }

    @GetMapping("/count-unread")
    public ResponseEntity<Map<String, Long>> countUnreadNotifications() {
        User user = authService.getCurrentUser();
        long count = notificationService.getUnreadNotifications(user.getId()).size();
        return ResponseEntity.ok(Map.of("count", count));
    }
}
