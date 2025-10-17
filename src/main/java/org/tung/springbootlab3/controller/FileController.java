package org.tung.springbootlab3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.tung.springbootlab3.model.Account;
import org.tung.springbootlab3.services.AccountService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/file-update")
public class FileController {
    @Autowired
    private AccountService  accountService;
    @PostMapping("/{accountId}/update-avatar")
    public ResponseEntity<Map<String, Object>> updateAvatar(
            @PathVariable UUID accountId,
            @RequestBody Map<String, String> body) {

        String imageUrl = body.get("imageUrl");
        String newPublicId = body.get("newPublicId");
        String oldPublicId = body.get("oldPublicId");

        Account acc = accountService.updateAvatar(accountId, imageUrl, newPublicId, oldPublicId);

        return ResponseEntity.ok(Map.of(
                "message", "Cập nhật avatar thành công!",
                "imageUrl", acc.getImage()
        ));
    }
}
