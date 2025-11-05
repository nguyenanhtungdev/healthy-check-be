package org.tung.springbootlab3.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tung.springbootlab3.model.Setting;
import org.tung.springbootlab3.repository.SettingRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AppSettingService {

    @Autowired
    private SettingRepository settingRepository;

    public Map<String, Object> getAllSettings() {
        Setting setting = settingRepository.findFirstByOrderByCreatedAtDesc();
        return setting != null ? setting.getAppSettings() : Map.of();
    }

    public Object getSettingByKey(String key) {
        Setting setting = settingRepository.findFirstByOrderByCreatedAtDesc();
        if (setting == null || setting.getAppSettings() == null) return null;
        return setting.getAppSettings().get(key);
    }

    public Setting updateSetting(Map<String, Object> body) {
        Setting setting = settingRepository.findFirstByOrderByCreatedAtDesc();
        if (setting == null) {
            setting = new Setting();
            setting.setId(UUID.randomUUID());
//            setting.setCreatedBy(UUID.fromString("8151dada-9e25-43d6-ae19-b778feedf74c"));
            setting.setCreatedAt(LocalDateTime.now());
        }

        Map<String, Object> appSettings = setting.getAppSettings() != null
                ? new HashMap<>(setting.getAppSettings())
                : new HashMap<>();

        appSettings.putAll(body);
        setting.setAppSettings(appSettings);
//        setting.setUpdatedBy(UUID.fromString("8151dada-9e25-43d6-ae19-b778feedf74c"));
        setting.setUpdatedAt(LocalDateTime.now());

        return settingRepository.save(setting);
    }
}
