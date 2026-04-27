package com.billkeeper.billkeeperbackend.submission;

import com.billkeeper.billkeeperbackend.bill.api.model.BillResponse;
import com.billkeeper.billkeeperbackend.bill.persistence.BillRepository;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.exception.BadRequestException;
import com.billkeeper.billkeeperbackend.exception.NotFoundException;
import com.billkeeper.billkeeperbackend.submission.api.model.CreateUpdateInsuranceSubmissionRequest;
import com.billkeeper.billkeeperbackend.submission.api.model.InsuranceSubmissionResponse;
import com.billkeeper.billkeeperbackend.submission.persistence.InsuranceSubmissionRepository;
import com.billkeeper.billkeeperbackend.submission.persistence.model.InsuranceSubmission;
import com.billkeeper.billkeeperbackend.user.api.model.UserResponse;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import com.billkeeper.billkeeperbackend.utils.BillUtils;
import com.billkeeper.billkeeperbackend.utils.accessmanager.AccessManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Transactional
@Service
public class InsuranceSubmissionService {
    private final InsuranceSubmissionRepository submissionRepository;
    private final BillRepository billRepository;
    private final BillUtils billUtils;

    public InsuranceSubmissionService(InsuranceSubmissionRepository submissionRepository, BillRepository billRepository, BillUtils billUtils) {
        this.submissionRepository = submissionRepository;
        this.billRepository = billRepository;
        this.billUtils = billUtils;
    }

    public List<InsuranceSubmissionResponse> getAllActiveSubmissions(User user) {
        return submissionRepository.findAllByActiveTrueOrderByDateTimeDesc(user)
                .stream()
                .map(this::buildSubmissionResponse)
                .toList();
    }

    public InsuranceSubmissionResponse getSubmission(UUID id, User user) {
        InsuranceSubmission submission = getSubmissionForUser(id, user);
        return buildSubmissionResponse(submission);
    }

    public void createSubmission(CreateUpdateInsuranceSubmissionRequest request, User user) {
        if (request.getName() == null || request.getName().isEmpty() || request.getBillIds().isEmpty()) {
            throw new BadRequestException("");
        }
        List<Bill> bills = request.getBillIds()
                .stream()
                .map(id -> billRepository.findByIdAndSubmissionNull(id)
                        .orElseThrow(() -> new BadRequestException("Bill not found or already assigned to a submission")))
                .toList();
        bills.forEach(bill -> AccessManager.checkIfUserCanAccessBillOrThrowException(user, bill));
        InsuranceSubmission submission = new InsuranceSubmission();
        submission.setActive(true);
        submission.setDateTime(OffsetDateTime.now());
        submission.setName(request.getName());
        submission.setUser(user);
        submission.setStatus(InsuranceSubmission.Status.OPEN);
        submissionRepository.save(submission);
        bills.forEach(bill -> {
            bill.setSubmission(submission);
            bill.setStatus(Bill.Status.FILING_IN_PROGRESS);
        });
        billRepository.saveAll(bills);
    }

    public void updateSubmission(UUID id, CreateUpdateInsuranceSubmissionRequest request, User user) {
        InsuranceSubmission submission = getSubmissionForUser(id, user);
        if (request.getName() != null && !request.getName().isEmpty()) {
            submission.setName(request.getName());
        }
        if (request.getEClaimId() != null && !request.getEClaimId().isEmpty()) {
            submission.setEClaimId(request.getEClaimId());
            List<Bill> bills = billRepository.findBySubmissionIdAndActiveTrueOrderByDateTimeDesc(submission.getId());
            bills.forEach(bill -> bill.setStatus(Bill.Status.FILED));
            billRepository.saveAll(bills);
        }
        if (request.getBillIds() != null) {
            updateSubmissionBills(submission, request.getBillIds());
        }
        submissionRepository.save(submission);
    }

    public void deleteSubmission(UUID id, User user) {
        InsuranceSubmission submission = getSubmissionForUser(id, user);
        applySubmissionDeletion(submission);
    }

    private void applySubmissionDeletion(InsuranceSubmission submission) {
        billRepository.findBySubmissionIdAndActiveTrueOrderByDateTimeDesc(submission.getId())
                .forEach(this::removeBillFromSubmission);
        submission.setActive(false);
        submissionRepository.save(submission);
    }

    private InsuranceSubmission getSubmissionForUser(UUID id, User user) {
        InsuranceSubmission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Submission not found"));
        AccessManager.checkIfUserCanAccessSubmissionOrThrowException(user, submission);
        return submission;
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
                .bills(bills.stream().map(BillResponse::new).toList())
                .totalUsdAmount(billUtils.getTotalBillsUsdAmount(bills))
                .reimbursedAmount(getTotalReimbursed(bills))
                .status(submission.getStatus())
                .user(new UserResponse(submission.getUser()))
                .build();
    }

    private Double getTotalReimbursed(List<Bill> bills) {
        return bills.stream()
                .filter(bill -> bill.getReimbursedAmount() != null)
                .mapToDouble(Bill::getReimbursedAmount)
                .sum();
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
        if (billsToRemoveFromSubmission.size() == submissionBills.size()) {
            applySubmissionDeletion(submission);
            return;
        }
        billsToAddToSubmission.forEach(bill -> bill.setSubmission(submission));
        billRepository.saveAll(billsToAddToSubmission);
        billsToRemoveFromSubmission.forEach(this::removeBillFromSubmission);
    }

    private void removeBillFromSubmission(Bill bill) {
        bill.setSubmission(null);
        bill.setStatus(Bill.Status.TO_FILE);
        billRepository.save(bill);
    }

    public void updateStatus(InsuranceSubmission submission) {
        List<Bill> bills = billRepository.findBySubmissionIdAndActiveTrueOrderByDateTimeDesc(submission.getId());
        boolean allClosed = bills.stream().allMatch(this::isBillClosed);
        submission.setStatus(allClosed ? InsuranceSubmission.Status.CLOSED : InsuranceSubmission.Status.OPEN);
        submissionRepository.save(submission);
    }

    private boolean isBillClosed(Bill bill) {
        return bill.getStatus().equals(Bill.Status.REIMBURSED) || bill.getStatus().equals(Bill.Status.REJECTED);
    }
}
