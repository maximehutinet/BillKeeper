package com.billkeeper.billkeeperbackend.settings.api.model;

import com.billkeeper.billkeeperbackend.settings.persistence.model.Settings;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
public class SettingsResponse {
    public UUID id;
    private OffsetDateTime dateTime;
    private Boolean active;
    private Double euroToUsdExchangeRate;
    private Double chfToUsdExchangeRate;

    public SettingsResponse(Settings settings) {
        this.id = settings.getId();
        this.dateTime = settings.getDateTime();
        this.active = settings.getActive();
        this.euroToUsdExchangeRate = settings.getEuroToUsdExchangeRate();
        this.chfToUsdExchangeRate = settings.getChfToUsdExchangeRate();
    }

}
