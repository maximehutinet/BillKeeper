package com.billkeeper.billkeeperbackend.utils.currency;

import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.settings.persistence.SettingsRepository;
import com.billkeeper.billkeeperbackend.settings.persistence.model.Settings;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillUtils {

    private final SettingsRepository settingsRepository;

    public BillUtils(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public Double getTotalBillsUsdAmount(List<Bill> bills) {
        Settings settings = this.settingsRepository.findLatestSettings()
                .orElse(null);
        if (settings == null) {
            return 0.0;
        }
        return bills
                .stream()
                .map(bill -> getBillUsdAmount(bill, settings))
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    private Double getBillUsdAmount(Bill bill, Settings settings) {
        if (bill.getCurrency() == null || bill.getAmount() == null) {
            return 0.0;
        }
        return switch (bill.getCurrency()) {
            case CHF -> bill.getAmount() * settings.getChfToUsdExchangeRate();
            case EUR -> bill.getAmount() * settings.getEuroToUsdExchangeRate();
        };

    }

}
