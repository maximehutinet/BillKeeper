package com.billkeeper.billkeeperbackend.stats.api;

import com.billkeeper.billkeeperbackend.bill.persistence.BillRepository;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.stats.api.model.StatsResponse;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import com.billkeeper.billkeeperbackend.utils.Authentication;
import com.billkeeper.billkeeperbackend.utils.BillUtils;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class StatsController {

    private final BillRepository billRepository;
    private final BillUtils billUtils;
    private final Authentication authentication;

    public StatsController(BillRepository billRepository, BillUtils billUtils, Authentication authentication) {
        this.billRepository = billRepository;
        this.billUtils = billUtils;
        this.authentication = authentication;
    }

    @GetMapping("/stats/bills")
    public StatsResponse getBillStats(JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
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
