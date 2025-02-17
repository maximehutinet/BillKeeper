package com.billkeeper.billkeeperbackend.stats.api;

import com.billkeeper.billkeeperbackend.bill.persistence.BillRepository;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.stats.api.model.StatsResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StatsController {

    private final BillRepository billRepository;

    public StatsController(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @GetMapping("/stats/bills")
    public StatsResponse getBillStats() {
        List<Bill> bills = billRepository.findAllByActiveTrueOrderByDateTimeDesc();
        return StatsResponse
                .builder()
                .totalActiveBills(bills.size())
                .build();
    }
}
