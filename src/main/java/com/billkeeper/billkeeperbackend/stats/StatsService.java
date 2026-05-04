package com.billkeeper.billkeeperbackend.stats;

import com.billkeeper.billkeeperbackend.bill.persistence.BillRepository;
import com.billkeeper.billkeeperbackend.stats.api.model.StatsResponse;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional(readOnly = true)
@Service
public class StatsService {

    private final BillRepository billRepository;

    public StatsService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    public StatsResponse getBillStats(User user) {
        UUID familyId = user.getFamily() != null ? user.getFamily().getId() : null;
        StatsProjection stats = billRepository.getStats(user.getId(), familyId);
        return StatsResponse
                .builder()
                .totalUsdAmountToPay(stats.getTotalToPay())
                .totalUsdAmountToBeReimbursed(stats.getTotalToBeReimbursed())
                .billToFileCount(stats.getToFileCount())
                .billInProgressCount(stats.getInProgressCount())
                .build();
    }
}
