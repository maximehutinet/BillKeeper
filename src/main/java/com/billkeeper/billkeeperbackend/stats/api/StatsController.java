package com.billkeeper.billkeeperbackend.stats.api;

import com.billkeeper.billkeeperbackend.bill.persistence.BillRepository;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.stats.api.model.StatsResponse;
import com.billkeeper.billkeeperbackend.utils.BillUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StatsController {

    private final BillRepository billRepository;
    private final BillUtils billUtils;

    public StatsController(BillRepository billRepository, BillUtils billUtils) {
        this.billRepository = billRepository;
        this.billUtils = billUtils;
    }

    @GetMapping("/stats/bills")
    public StatsResponse getBillStats() {
        List<Bill> billsWaitingToBeReimbursed = billRepository.findAllByActiveTrueAndStatus(Bill.Status.FILED);
        List<Bill> billsToPay = billRepository.findAllByActiveTrueAndPaidDateTimeNull();
        return StatsResponse
                .builder()
                .totalUsdAmountToPay(billUtils.getTotalBillsUsdAmount(billsToPay))
                .totalUsdAmountToBeReimbursed(billUtils.getTotalBillsUsdAmount(billsWaitingToBeReimbursed))
                .billToFileCount(billRepository.countByActiveTrueAndStatus(Bill.Status.TO_FILE))
                .billInProgressCount(billRepository.countByActiveTrueAndStatus(Bill.Status.FILED))
                .build();
    }
}
