package com.billkeeper.billkeeperbackend.bill;

import com.billkeeper.billkeeperbackend.beneficiary.BeneficiaryRepository;
import com.billkeeper.billkeeperbackend.bill.api.model.UpdateBillReimbursementRequest;
import com.billkeeper.billkeeperbackend.bill.api.model.UpdateBillRequest;
import com.billkeeper.billkeeperbackend.bill.persistence.BillRepository;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.document.persistence.DocumentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@Transactional
public class BillService {

    private final BillRepository billRepository;
    private final BeneficiaryRepository beneficiaryRepository;

    private final DocumentRepository documentRepository;

    public BillService(BillRepository billRepository, BeneficiaryRepository beneficiaryRepository, DocumentRepository documentRepository) {
        this.billRepository = billRepository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.documentRepository = documentRepository;
    }

    public void update(Bill bill, UpdateBillRequest request) {
        Optional.ofNullable(request.getBeneficiary())
                .flatMap(b -> beneficiaryRepository.findById(b.getId()))
                .ifPresent(bill::setBeneficiary);
        if (request.getName() != null) bill.setName(request.getName());
        if (request.getAmount() != null) bill.setAmount(request.getAmount());
        if (request.getCurrency() != null) bill.setCurrency(request.getCurrency());
        if (request.getProvider() != null) bill.setProvider(request.getProvider());
        if (request.getStatus() != null) bill.setStatus(request.getStatus());
        if (request.getServiceDateTime() != null) bill.setServiceDateTime(request.getServiceDateTime());
        applyPaymentUpdate(bill, request.getPaidDateTime());
        billRepository.save(bill);
    }

    public void updatePayment(Bill bill, OffsetDateTime paymentDateTime) {
        applyPaymentUpdate(bill, paymentDateTime);
        billRepository.save(bill);
    }

    private void applyPaymentUpdate(Bill bill, OffsetDateTime paymentDateTime) {
        bill.setPaidDateTime(paymentDateTime);
        if (paymentDateTime != null && bill.getStatus() == Bill.Status.TO_PAY) {
            bill.setStatus(Bill.Status.TO_FILE);
        }
    }

    public void updateReimbursement(Bill bill, UpdateBillReimbursementRequest request) {
        bill.setReimbursementDateTime(request.getReimbursementDateTime());
        bill.setReimbursedAmount(request.getReimbursedAmount());
        if (bill.getStatus() != Bill.Status.REIMBURSED) {
            bill.setStatus(Bill.Status.REIMBURSEMENT_IN_PROGRESS);
        }
        billRepository.save(bill);
    }

    public void updateStatus(Bill bill, Bill.Status status) {
        if (status != null) {
            bill.setStatus(status);
            billRepository.save(bill);
        }
    }

    public void delete(Bill bill) {
        documentRepository.deactivateByBillId(bill.getId());
        bill.setActive(false);
        billRepository.save(bill);
    }
}
