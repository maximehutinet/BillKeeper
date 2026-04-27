package com.billkeeper.billkeeperbackend.bill;

import com.billkeeper.billkeeperbackend.AppConfig;
import com.billkeeper.billkeeperbackend.beneficiary.persistence.BeneficiaryRepository;
import com.billkeeper.billkeeperbackend.bill.api.model.BillResponse;
import com.billkeeper.billkeeperbackend.bill.api.model.UpdateBillReimbursementRequest;
import com.billkeeper.billkeeperbackend.bill.api.model.UpdateBillRequest;
import com.billkeeper.billkeeperbackend.bill.persistence.BillRepository;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.document.DocumentService;
import com.billkeeper.billkeeperbackend.exception.InternalServerErrorException;
import com.billkeeper.billkeeperbackend.exception.NotFoundException;
import com.billkeeper.billkeeperbackend.parsingjob.ParsingJobService;
import com.billkeeper.billkeeperbackend.parsingjob.persistence.model.ParsingJob;
import com.billkeeper.billkeeperbackend.submission.InsuranceSubmissionService;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import com.billkeeper.billkeeperbackend.utils.parsing.BillParsingService;
import com.billkeeper.billkeeperbackend.utils.security.accessmanager.AccessManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class BillService {

    private final AccessManager accessManager;
    private final BillRepository billRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final DocumentService documentService;
    private final BillParsingService billParsingService;
    private final ParsingJobService parsingJobService;
    private final AppConfig appConfig;
    private final InsuranceSubmissionService insuranceSubmissionService;
    private final Logger logger = LoggerFactory.getLogger(BillService.class);

    public BillService(AccessManager accessManager, BillRepository billRepository, BeneficiaryRepository beneficiaryRepository, DocumentService documentService, BillParsingService billParsingService, ParsingJobService parsingJobService, AppConfig appConfig, InsuranceSubmissionService insuranceSubmissionService) {
        this.accessManager = accessManager;
        this.billRepository = billRepository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.documentService = documentService;
        this.billParsingService = billParsingService;
        this.parsingJobService = parsingJobService;
        this.appConfig = appConfig;
        this.insuranceSubmissionService = insuranceSubmissionService;
    }

    public Bill getBillForUser(UUID id, User user) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bill not found"));
        accessManager.checkIfUserCanAccessBillOrThrowException(user, bill);
        return bill;
    }

    public void checkUserCanAccessBill(UUID billId, User user) {
        getBillForUser(billId, user);
    }

    public List<BillResponse> findAllBills(User user) {
        return billRepository.findAllActiveBills(user);
    }

    public BillResponse findBillById(UUID id, User user) {
        return billRepository.findBillById(id, user)
                .orElseThrow(() -> new NotFoundException("Bill not found"));
    }

    public void createBill(MultipartFile multipartFile, User user) {
        try {
            String filename = UUID.randomUUID() + ".pdf";
            Path destination = Paths.get(appConfig.getDocumentsDirectory()).resolve(filename);
            multipartFile.transferTo(destination);
            Bill bill = createEmptyBill(user);
            documentService.create(filename, bill, user);
            ParsingJob parsingJob = parsingJobService.create(bill);
            billParsingService.parseAndUpdateBill(bill, destination.toFile(), parsingJob);
        } catch (IOException | RuntimeException e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException("Error while uploading file");
        }
    }

    private Bill createEmptyBill(User user) {
        Bill bill = new Bill();
        bill.setActive(true);
        bill.setDateTime(OffsetDateTime.now());
        bill.setStatus(Bill.Status.TO_PAY);
        bill.setUser(user);
        billRepository.save(bill);
        return bill;
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
        if (bill.getSubmission() != null) {
            insuranceSubmissionService.updateStatus(bill.getSubmission());
        }
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
            if (bill.getSubmission() != null) {
                insuranceSubmissionService.updateStatus(bill.getSubmission());
            }
        }
    }

    public void delete(Bill bill) {
        documentService.deactivateForBill(bill.getId());
        bill.setActive(false);
        billRepository.save(bill);
    }

    public List<String> findAllProvidersMatchingValue(String provider, User user) {
        return billRepository.findAllProvidersMatchingValue(provider, user);
    }
}
