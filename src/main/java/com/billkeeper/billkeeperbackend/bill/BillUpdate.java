package com.billkeeper.billkeeperbackend.bill;

import com.billkeeper.billkeeperbackend.bill.persistence.BillRepository;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import org.springframework.stereotype.Service;

@Service
public class BillUpdate {

    private final BillRepository billRepository;

    public BillUpdate(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    public void update(Bill bill, Bill updatedBill) {
        if (updatedBill.getName() != null && !updatedBill.getName().equals(bill.getName())) {
            bill.setName(updatedBill.getName());
        }
        if (updatedBill.getAmount() != null && !updatedBill.getAmount().equals(bill.getAmount())) {
            bill.setAmount(updatedBill.getAmount());
        }
        if (updatedBill.getCurrency() != null && !updatedBill.getCurrency().equals(bill.getCurrency())) {
            bill.setCurrency(updatedBill.getCurrency());
        }
        if (updatedBill.getProvider() != null && !updatedBill.getProvider().equals(bill.getProvider())) {
            bill.setProvider(updatedBill.getProvider());
        }
        if (updatedBill.getStatus() != null && !updatedBill.getStatus().equals(bill.getStatus())) {
            bill.setStatus(updatedBill.getStatus());
        }
        if (updatedBill.getBeneficiary() != null && !updatedBill.getBeneficiary().equals(bill.getBeneficiary())) {
            bill.setBeneficiary(updatedBill.getBeneficiary());
        }
        if (updatedBill.getPaidDateTime() != null && !updatedBill.getPaidDateTime().equals(bill.getPaidDateTime())) {
            bill.setPaidDateTime(updatedBill.getPaidDateTime());
        }
        billRepository.save(bill);
    }
}
