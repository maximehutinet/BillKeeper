package com.billkeeper.billkeeperbackend.utils;

import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.settings.persistence.SettingsRepository;
import com.billkeeper.billkeeperbackend.settings.persistence.model.Settings;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillUtils {

    private final SettingsRepository settingsRepository;
    private Settings settings;

    public BillUtils(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public Double getTotalBillsUsdAmount(List<Bill> bills) {
        this.settings = this.settingsRepository.findLatestSettings()
                .orElse(null);
        if (this.settings == null) {
            return null;
        }
        return bills
                .stream()
                .map(this::getBillUsdAmount)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    private Double getBillUsdAmount(Bill bill) {
        if (bill.getCurrency() == null) {
            return 0.0;
        }
        return switch (bill.getCurrency()) {
            case CHF -> bill.getAmount() * settings.getChfToUsdExchangeRate();
            case EUR -> bill.getAmount() * settings.getEuroToUsdExchangeRate();
        };

    }

}
