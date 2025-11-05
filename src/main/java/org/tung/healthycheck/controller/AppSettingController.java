package org.tung.healthycheck.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tung.healthycheck.dto.ApiResponse;
import org.tung.healthycheck.model.Setting;
import org.tung.healthycheck.services.AppSettingService;

import java.util.Map;

import static org.tung.healthycheck.constants.AppMessages.CONFIG_UPDATE_SUCCESS;
import static org.tung.healthycheck.constants.AppMessages.SYSTEM_ERROR;

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
            return ResponseEntity.ok(ApiResponse.success(CONFIG_UPDATE_SUCCESS, updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.fail(SYSTEM_ERROR));
        }
    }
}
