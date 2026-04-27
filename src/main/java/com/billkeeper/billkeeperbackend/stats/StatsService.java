package com.billkeeper.billkeeperbackend.stats;

import com.billkeeper.billkeeperbackend.bill.persistence.BillRepository;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.stats.api.model.StatsResponse;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import com.billkeeper.billkeeperbackend.utils.currency.BillUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Transactional
@Service
public class StatsService {

    private final BillRepository billRepository;
    private final BillUtils billUtils;

    public StatsService(BillRepository billRepository, BillUtils billUtils) {
        this.billRepository = billRepository;
        this.billUtils = billUtils;
    }

    public StatsResponse getBillStats(User user) {
        List<Bill> billsWaitingToBeReimbursed = billRepository.findAllByActiveTrueAndStatus(Arrays.asList(Bill.Status.TO_FILE, Bill.Status.FILED), user);
        List<Bill> billsToPay = billRepository.findAllByActiveTrueAndPaidDateTimeNull(user);
        return StatsResponse
                .builder()
                .totalUsdAmountToPay(billUtils.getTotalBillsUsdAmount(billsToPay))
                .totalUsdAmountToBeReimbursed(billUtils.getTotalBillsUsdAmount(billsWaitingToBeReimbursed))
                .billToFileCount(billRepository.countByActiveTrueAndStatus(Bill.Status.TO_FILE, user))
                .billInProgressCount(billRepository.countByActiveTrueAndStatus(Bill.Status.FILED, user))
                .build();
    }
}
