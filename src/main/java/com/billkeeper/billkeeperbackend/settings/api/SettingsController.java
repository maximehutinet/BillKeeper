package com.billkeeper.billkeeperbackend.settings.api;

import com.billkeeper.billkeeperbackend.settings.SettingsService;
import com.billkeeper.billkeeperbackend.settings.api.model.CreateUpdateSettingsRequest;
import com.billkeeper.billkeeperbackend.settings.api.model.SettingsResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping("/settings")
    public SettingsResponse getSettings() {
        return settingsService.getSettings();
    }

    @PostMapping("/settings")
    public void applySettings(@RequestBody CreateUpdateSettingsRequest request) {
        settingsService.applySettings(request);
    }
}
