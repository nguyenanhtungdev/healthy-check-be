package org.tung.healthycheck.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tung.healthycheck.dto.EmailRequest;
import org.tung.healthycheck.dto.ForgotPasswordRequest;
import org.tung.healthycheck.dto.RegisterRequest;
import org.tung.healthycheck.dto.ResetPasswordRequest;
import org.tung.healthycheck.model.Account;
import org.tung.healthycheck.model.Role;
import org.tung.healthycheck.model.User;
import org.tung.healthycheck.services.AuthService;
import org.tung.healthycheck.services.UserService;
import org.tung.healthycheck.util.JwtUtil;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    // Gửi mã xác thực
    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@RequestBody EmailRequest request) {
        String email = request.getEmail().trim();
        authService.sendRegisterCode(email);
        return ResponseEntity.ok(Map.of("message", "Verification code sent to " + email));
    }

    // Xác thực và đăng ký
    @PostMapping("/verify-register")
    public ResponseEntity<?> verifyAndRegister(@RequestBody RegisterRequest request) {
        Account acc = authService.verifyAndRegister(
                request.getEmail().trim(),
                request.getPassword(),
                request.getCode().trim()
        );

        Map<String, Object> res = new HashMap<>();
        res.put("message", "Register successful");
        res.put("accountId", acc.getId());
        return ResponseEntity.ok(res);
    }

    // Gửi mã đổi mật khẩu
    @PostMapping("/forgot-password")
    public ResponseEntity<?> sendForgotCode(@RequestBody ForgotPasswordRequest request) {
        String email = request.getEmail().trim();
        authService.sendRegisterCode(email);
        return ResponseEntity.ok(Map.of("message", "Password reset code sent"));
    }

    // Xác thực đổi mật khẩu
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(
                request.getEmail().trim(),
                request.getNewPassword(),
                request.getCode().trim()
        );
        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Account account) {
        Account validAccount = authService.validate(account.getUsername(), account.getPassword());
        User user = userService.getUserByAccountId(validAccount.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng cho account id: " + validAccount.getId()));
        String token = jwtUtil.generateToken(validAccount.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("token", token);
        response.put("accountId", validAccount.getId());
        response.put("userId", user.getId());
        response.put("username", validAccount.getUsername());
        response.put("fullName", user.getFullName());
        response.put("roles", validAccount.getRoles()
                .stream()
                .map(Role::getRoleName)
                .toList());
        response.put("status", validAccount.getStatus());
        response.put("createdAt", validAccount.getCreatedAt());

        return ResponseEntity.ok(response);

    }

    @PostMapping("/check-username")
    public ResponseEntity<Map<String, Object>> checkUsername(@RequestBody Map<String, String> request) {
        String username = request.get("username").trim();

        boolean exists = authService.usernameExists(username);
        Map<String, Object> response = new HashMap<>();

        if (exists) {
            response.put("message", "Username already exists, please use another email.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } else {
            response.put("message", "Username available.");
            return ResponseEntity.ok(response);
        }
    }
}
