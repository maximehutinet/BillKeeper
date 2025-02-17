package com.billkeeper.billkeeperbackend.settings.api;

import com.billkeeper.billkeeperbackend.settings.api.model.CreateUpdateSettingsRequest;
import com.billkeeper.billkeeperbackend.settings.persistence.SettingsRepository;
import com.billkeeper.billkeeperbackend.settings.persistence.model.Settings;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
public class SettingsController {

    private final SettingsRepository settingsRepository;
    public SettingsController(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @GetMapping("/settings")
    public Settings getSettings() {
        return this.settingsRepository.findLatestSettings()
                .orElse(null);
    }

    @PostMapping("/settings")
    public void createUpdateSettings(@RequestBody CreateUpdateSettingsRequest request) {
        this.settingsRepository.findLatestSettings().ifPresent(oldSettings -> {
            oldSettings.setActive(false);
            settingsRepository.save(oldSettings);
        });
        Settings settings = new Settings();
        settings.setDateTime(OffsetDateTime.now());
        settings.setActive(true);
        settings.setChfToUsdExchangeRate(request.getChfToUsdExchangeRate());
        settings.setEuroToUsdExchangeRate(request.getEuroToUsdExchangeRate());
        settingsRepository.save(settings);
    }
}
