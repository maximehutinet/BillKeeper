package com.billkeeper.billkeeperbackend.bill;

import com.billkeeper.billkeeperbackend.beneficiary.BeneficiaryRepository;
import com.billkeeper.billkeeperbackend.bill.persistence.BillRepository;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import org.springframework.stereotype.Service;

@Service
public class BillUpdate {

    private final BillRepository billRepository;
    private final BeneficiaryRepository beneficiaryRepository;

    public BillUpdate(BillRepository billRepository, BeneficiaryRepository beneficiaryRepository) {
        this.billRepository = billRepository;
        this.beneficiaryRepository = beneficiaryRepository;
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
            beneficiaryRepository.findById(updatedBill.getBeneficiary().getId()).ifPresent(bill::setBeneficiary);
        }
        bill.setPaidDateTime(updatedBill.getPaidDateTime());
        billRepository.save(bill);
    }
}
