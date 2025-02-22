package com.billkeeper.billkeeperbackend.submission.api;

import com.billkeeper.billkeeperbackend.bill.persistence.BillRepository;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.exception.BadRequestException;
import com.billkeeper.billkeeperbackend.exception.NotFoundException;
import com.billkeeper.billkeeperbackend.submission.api.model.CreateUpdateInsuranceSubmissionRequest;
import com.billkeeper.billkeeperbackend.submission.api.model.InsuranceSubmissionResponse;
import com.billkeeper.billkeeperbackend.submission.persistence.InsuranceSubmissionRepository;
import com.billkeeper.billkeeperbackend.submission.persistence.model.InsuranceSubmission;
import com.billkeeper.billkeeperbackend.utils.BillUtils;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
public class InsuranceSubmissionController {

    private final InsuranceSubmissionRepository insuranceSubmissionRepository;
    private final BillRepository billRepository;
    private final BillUtils billUtils;

    public InsuranceSubmissionController(InsuranceSubmissionRepository insuranceSubmissionRepository, BillRepository billRepository, BillUtils billUtils) {
        this.insuranceSubmissionRepository = insuranceSubmissionRepository;
        this.billRepository = billRepository;
        this.billUtils = billUtils;
    }

    @GetMapping("submissions")
    public List<InsuranceSubmissionResponse> getAllActiveSubmissions() {
        List<InsuranceSubmission> submissions = insuranceSubmissionRepository.findAllByActiveTrueOrderByDateTimeDesc();
        return submissions
                .stream()
                .map(this::buildSubmissionResponse)
                .toList();
    }

    @GetMapping("submissions/{id}")
    public InsuranceSubmissionResponse getSubmission(@PathVariable UUID id) {
        InsuranceSubmission submission = insuranceSubmissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Submission not found"));
        return buildSubmissionResponse(submission);
    }

    @PostMapping("/submissions")
    public void createSubmission(@RequestBody CreateUpdateInsuranceSubmissionRequest request) {
        if (request.getName() == null || request.getName().isEmpty() || request.getBillIds().isEmpty()) {
            throw new BadRequestException("");
        }
        List<Bill> bills = request.getBillIds()
                .stream()
                .map(id -> billRepository.findByIdAndSubmissionNull(id).orElse(null))
                .toList();
        if (bills.contains(null)) {
            throw new BadRequestException("");
        }
        InsuranceSubmission submission = new InsuranceSubmission();
        submission.setActive(true);
        submission.setDateTime(OffsetDateTime.now());
        submission.setName(request.getName());
        insuranceSubmissionRepository.save(submission);
        bills.forEach(bill -> addBillToSubmission(submission, bill));
    }

    @PostMapping("/submissions/{id}")
    public void updateSubmission(@PathVariable UUID id, @RequestBody CreateUpdateInsuranceSubmissionRequest request) {
        InsuranceSubmission submission = insuranceSubmissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Submission not found"));
        if (request.getName() != null && !request.getName().isEmpty() && !request.getName().equals(submission.getName())) {
            submission.setName(request.getName());
        }
        if (request.getEClaimId() != null && !request.getEClaimId().isEmpty()) {
            submission.setEClaimId(request.getEClaimId());
        }
        if (request.getBillIds() != null) {
            updateSubmissionBills(submission, request.getBillIds());
        }
        insuranceSubmissionRepository.save(submission);
    }

    @DeleteMapping("submissions/{id}")
    public void deleteSubmission(@PathVariable UUID id) {
        InsuranceSubmission submission = insuranceSubmissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Submission not found"));
        billRepository.findBySubmissionIdAndActiveTrueOrderByDateTimeDesc(submission.getId())
                        .forEach(this::removeBillFromSubmission);
        submission.setActive(false);
        insuranceSubmissionRepository.save(submission);
    }

    private InsuranceSubmissionResponse buildSubmissionResponse(InsuranceSubmission submission) {
        List<Bill> bills = billRepository.findBySubmissionIdAndActiveTrueOrderByDateTimeDesc(submission.getId());
        return InsuranceSubmissionResponse
                .builder()
                .id(submission.getId())
                .active(submission.getActive())
                .dateTime(submission.getDateTime())
                .name(submission.getName())
                .eClaimId(submission.getEClaimId())
                .bills(bills)
                .totalUsdAmount(billUtils.getTotalBillsUsdAmount(bills))
                .build();
    }

    private void updateSubmissionBills(InsuranceSubmission submission, List<UUID> updatedSubmissionBills) {
        List<Bill> submissionBills = billRepository.findBySubmissionIdAndActiveTrueOrderByDateTimeDesc(submission.getId());
        List<UUID> submissionsBillIds = submissionBills
                .stream()
                .map(Bill::getId)
                .toList();
        List<Bill> billsToAddToSubmission = updatedSubmissionBills
                .stream()
                .filter(billId -> !submissionsBillIds.contains(billId))
                .map(billId -> billRepository.findByIdAndSubmissionNull(billId).orElseThrow(() -> new NotFoundException("Bill not found")))
                .toList();
        List<Bill> billsToRemoveFromSubmission = submissionBills
                .stream()
                .filter(bill -> !updatedSubmissionBills.contains(bill.getId()))
                .toList();
        billsToAddToSubmission.forEach(bill -> addBillToSubmission(submission, bill));
        billsToRemoveFromSubmission.forEach(this::removeBillFromSubmission);
    }

    private void removeBillFromSubmission(Bill bill) {
        bill.setSubmission(null);
        bill.setStatus(Bill.Status.TO_FILE);
        billRepository.save(bill);
    }

    private void addBillToSubmission(InsuranceSubmission submission, Bill bill) {
        bill.setSubmission(submission);
        bill.setStatus(Bill.Status.FILED);
        billRepository.save(bill);
    }

}