package com.billkeeper.billkeeperbackend.submission.api;

import com.billkeeper.billkeeperbackend.bill.persistence.BillRepository;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.exception.BadRequestException;
import com.billkeeper.billkeeperbackend.exception.NotFoundException;
import com.billkeeper.billkeeperbackend.submission.api.model.CreateUpdateInsuranceSubmissionRequest;
import com.billkeeper.billkeeperbackend.submission.api.model.InsuranceSubmissionResponse;
import com.billkeeper.billkeeperbackend.submission.persistence.InsuranceSubmissionRepository;
import com.billkeeper.billkeeperbackend.submission.persistence.model.InsuranceSubmission;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
public class InsuranceSubmissionController {

    private final InsuranceSubmissionRepository insuranceSubmissionRepository;
    private final BillRepository billRepository;

    public InsuranceSubmissionController(InsuranceSubmissionRepository insuranceSubmissionRepository, BillRepository billRepository) {
        this.insuranceSubmissionRepository = insuranceSubmissionRepository;
        this.billRepository = billRepository;
    }

    @GetMapping("submissions")
    public List<InsuranceSubmissionResponse> getAllActiveSubmissions() {
        List<InsuranceSubmission> submissions = insuranceSubmissionRepository.findAllByActiveTrueOrderByDateTimeDesc();
        return submissions
                .stream()
                .map(submission -> {
                    return InsuranceSubmissionResponse
                            .builder()
                            .id(submission.getId())
                            .active(submission.getActive())
                            .dateTime(submission.getDateTime())
                            .name(submission.getName())
                            .bills(billRepository.findBySubmissionIdAndActiveTrueOrderByDateTimeDesc(submission.getId()))
                            .build();
                })
                .toList();
    }

    @GetMapping("submissions/{id}")
    public InsuranceSubmission getSubmission(@PathVariable UUID id) {
        return insuranceSubmissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Submission not found"));
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
        for (Bill bill : bills) {
            bill.setSubmission(submission);
            bill.setStatus(Bill.Status.FILED);
            billRepository.save(bill);
        }
    }

    @PostMapping("/submissions/{id}")
    public void updateSubmission(@PathVariable UUID id, @RequestBody CreateUpdateInsuranceSubmissionRequest request) {
        InsuranceSubmission submission = insuranceSubmissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Submission not found"));
        submission.setName(request.getName());
        insuranceSubmissionRepository.save(submission);
    }

    @DeleteMapping("submissions/{id}")
    public void deleteSubmission(@PathVariable UUID id) {
        InsuranceSubmission submission = insuranceSubmissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Submission not found"));
        submission.setActive(false);
        insuranceSubmissionRepository.save(submission);
    }
}