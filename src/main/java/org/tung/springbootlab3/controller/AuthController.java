package org.tung.springbootlab3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tung.springbootlab3.model.Account;
import org.tung.springbootlab3.model.User;
import org.tung.springbootlab3.services.AuthService;
import org.tung.springbootlab3.util.JwtUtil;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private JwtUtil jwtUtil;

    // Gửi mã xác thực
    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@RequestParam String email) {
        authService.sendRegisterCode(email);
        return ResponseEntity.ok(Map.of("message", "Verification code sent to " + email));
    }

    // Xác thực và đăng ký
    @PostMapping("/verify-register")
    public ResponseEntity<?> verifyAndRegister(@RequestParam String email,
                                               @RequestParam String password,
                                               @RequestParam String code) {
        Account acc = authService.verifyAndRegister(email, password, code);
        Map<String, Object> res = new HashMap<>();
        res.put("message", "Register successful");
        res.put("accountId", acc.getId());
        return ResponseEntity.ok(res);
    }

    // Gửi mã đổi mật khẩu
    @PostMapping("/forgot-password")
    public ResponseEntity<?> sendForgotCode(@RequestParam String email) {
        authService.sendRegisterCode(email);
        return ResponseEntity.ok(Map.of("message", "Password reset code sent"));
    }

    // Xác thực đổi mật khẩu
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String email,
                                           @RequestParam String newPassword,
                                           @RequestParam String code) {
        authService.resetPassword(email, newPassword, code);
        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Account account) {
        Account savedAccount = authService.register(account.getUsername(), account.getPassword());
        Map<String,Object> response = new HashMap<>();
        response.put("message", "Register successful");
        response.put("accountId", savedAccount.getId());

        return ResponseEntity.ok(response);
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Account account) {
        Account validAccount = authService.validate(account.getUsername(), account.getPassword());

        if (validAccount != null) {
            String token = jwtUtil.generateToken(validAccount.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("token", token);
            response.put("accountId", validAccount.getId());
            response.put("username", validAccount.getUsername());
            response.put("roles", validAccount.getRoles()
                    .stream()
                    .map(role -> role.getRoleName())
                    .toList());
            response.put("status", validAccount.getStatus());
            response.put("createdAt", validAccount.getCreatedAt());

            return ResponseEntity.ok(response);
        }

        Map<String, Object> error = new HashMap<>();
        error.put("message", "Invalid username or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

}
