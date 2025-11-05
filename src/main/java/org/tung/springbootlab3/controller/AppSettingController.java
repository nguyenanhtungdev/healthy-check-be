package org.tung.springbootlab3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tung.springbootlab3.dto.ApiResponse;
import org.tung.springbootlab3.model.Setting;
import org.tung.springbootlab3.services.AppSettingService;

import java.util.Map;

import static org.tung.springbootlab3.constants.AppMessages.CONFIG_UPDATE_SUCCESS;
import static org.tung.springbootlab3.constants.AppMessages.SYSTEM_ERROR;

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
