package com.billkeeper.billkeeperbackend.settings;

import com.billkeeper.billkeeperbackend.settings.api.model.CreateUpdateSettingsRequest;
import com.billkeeper.billkeeperbackend.settings.api.model.SettingsResponse;
import com.billkeeper.billkeeperbackend.settings.persistence.SettingsRepository;
import com.billkeeper.billkeeperbackend.settings.persistence.model.Settings;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class SettingsService {

    private final SettingsRepository settingsRepository;

    public SettingsService(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public SettingsResponse getSettings() {
        return settingsRepository.findLatestSettings()
                .map(SettingsResponse::new)
                .orElse(null);
    }

    public void applySettings(CreateUpdateSettingsRequest request) {
        settingsRepository.findLatestSettings().ifPresent(oldSettings -> oldSettings.setActive(false));
        Settings settings = new Settings();
        settings.setDateTime(OffsetDateTime.now());
        settings.setActive(true);
        settings.setChfToUsdExchangeRate(request.getChfToUsdExchangeRate());
        settings.setEuroToUsdExchangeRate(request.getEuroToUsdExchangeRate());
        settingsRepository.save(settings);
    }
}
