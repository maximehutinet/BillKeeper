package com.billkeeper.billkeeperbackend.submission;

import com.billkeeper.billkeeperbackend.bill.persistence.BillRepository;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.submission.persistence.InsuranceSubmissionRepository;
import com.billkeeper.billkeeperbackend.submission.persistence.model.InsuranceSubmission;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InsuranceSubmissionUpdate {

    private final InsuranceSubmissionRepository insuranceSubmissionRepository;
    private final BillRepository billRepository;

    public InsuranceSubmissionUpdate(InsuranceSubmissionRepository insuranceSubmissionRepository, BillRepository billRepository) {
        this.insuranceSubmissionRepository = insuranceSubmissionRepository;
        this.billRepository = billRepository;
    }

    public void updateStatus(InsuranceSubmission submission) {
        List<Bill> bills = billRepository.findBySubmissionIdAndActiveTrueOrderByDateTimeDesc(submission.getId());
        InsuranceSubmission.Status status = bills.stream()
                .filter(this::billClosed)
                .count() == (long) bills.size() ? InsuranceSubmission.Status.CLOSED : InsuranceSubmission.Status.OPEN;
        submission.setStatus(status);
        insuranceSubmissionRepository.save(submission);
    }

    private Boolean billClosed(Bill bill) {
        return bill.getStatus().equals(Bill.Status.REIMBURSED) || bill.getStatus().equals(Bill.Status.REJECTED);
    }
}
