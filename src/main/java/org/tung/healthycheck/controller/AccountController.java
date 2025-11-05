package org.tung.healthycheck.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tung.healthycheck.dto.AccountProfileDTO;
import org.tung.healthycheck.dto.UpdateProfileDTO;
import org.tung.healthycheck.services.AccountService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;
    @PostMapping("/get-account")
    public ResponseEntity<AccountProfileDTO> getAccount(@RequestBody Map<String, String> body) {
        String idString = body.get("id");
        UUID id = UUID.fromString(idString);
        return ResponseEntity.ok(accountService.getAccount(id));
    }

    @PostMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileDTO dto) {
        try {
            String message = accountService.updateProfile(dto);
            return ResponseEntity.ok(Map.of("message", message));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Lỗi hệ thống: " + e.getMessage()));
        }
    }
}
