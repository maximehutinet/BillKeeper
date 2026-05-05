package com.billkeeper.billkeeperbackend.settings.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateUpdateSettingsRequest {
    private Double euroToUsdExchangeRate;
    private Double chfToUsdExchangeRate;
}