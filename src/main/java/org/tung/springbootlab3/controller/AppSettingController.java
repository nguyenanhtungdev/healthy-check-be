package org.tung.springbootlab3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tung.springbootlab3.model.Setting;
import org.tung.springbootlab3.services.AppSettingService;

import java.util.Map;

@RestController
@RequestMapping("/app-settings")
public class AppSettingController {

    @Autowired
    private AppSettingService appSettingService;

    @GetMapping
    public ResponseEntity<?> getAllSettings() {
        return ResponseEntity.ok(appSettingService.getAllSettings());
    }

    @GetMapping("/{key}")
    public ResponseEntity<?> getSettingByKey(@PathVariable String key) {
        Object value = appSettingService.getSettingByKey(key);
        if (value == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of(key, value));
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateSetting(@RequestBody Map<String, Object> body) {
        try {
            Setting updated = appSettingService.updateSetting(body);
            return ResponseEntity.ok(Map.of(
                    "message", "Cập nhật cấu hình thành công",
                    "data", updated
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Lỗi hệ thống: " + e.getMessage()));
        }
    }
}
