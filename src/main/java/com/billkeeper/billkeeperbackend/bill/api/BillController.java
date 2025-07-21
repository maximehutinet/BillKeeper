package com.billkeeper.billkeeperbackend.bill.api;

import com.billkeeper.billkeeperbackend.AppConfig;
import com.billkeeper.billkeeperbackend.bill.BillDeletion;
import com.billkeeper.billkeeperbackend.bill.BillUpdate;
import com.billkeeper.billkeeperbackend.bill.api.model.BillResponse;
import com.billkeeper.billkeeperbackend.bill.persistence.BillRepository;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.document.DocumentCreation;
import com.billkeeper.billkeeperbackend.document.api.CreateDocumentResponse;
import com.billkeeper.billkeeperbackend.document.api.model.DocumentResponse;
import com.billkeeper.billkeeperbackend.document.persistence.DocumentRepository;
import com.billkeeper.billkeeperbackend.exception.InternalServerErrorException;
import com.billkeeper.billkeeperbackend.exception.NotFoundException;
import com.billkeeper.billkeeperbackend.parsingjob.persistence.ParsingJobRepository;
import com.billkeeper.billkeeperbackend.parsingjob.persistence.model.ParsingJob;
import com.billkeeper.billkeeperbackend.submission.InsuranceSubmissionUpdate;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import com.billkeeper.billkeeperbackend.utils.Authentication;
import com.billkeeper.billkeeperbackend.utils.BillParsingService;
import com.billkeeper.billkeeperbackend.utils.accessmanager.AccessManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
public class BillController {

    private final BillRepository billRepository;
    private final BillParsingService billParsingService;
    private final DocumentRepository documentRepository;
    private final AppConfig appConfig;
    private final BillUpdate billUpdate;
    private final BillDeletion billDeletion;
    private final CreateDocumentResponse createDocumentResponse;
    private final Logger logger = LoggerFactory.getLogger(BillController.class);
    private final ParsingJobRepository parsingJobRepository;
    private final Authentication authentication;
    private final InsuranceSubmissionUpdate insuranceSubmissionUpdate;
    private final DocumentCreation documentCreation;

    public BillController(BillRepository billRepository, DocumentRepository documentRepository, AppConfig appConfig, BillParsingService billParsingService, BillUpdate billUpdate, BillDeletion billDeletion, CreateDocumentResponse createDocumentResponse, ParsingJobRepository parsingJobRepository, Authentication authentication, InsuranceSubmissionUpdate insuranceSubmissionUpdate, DocumentCreation documentCreation) {
        this.billRepository = billRepository;
        this.documentRepository = documentRepository;
        this.appConfig = appConfig;
        this.billParsingService = billParsingService;
        this.billUpdate = billUpdate;
        this.billDeletion = billDeletion;
        this.createDocumentResponse = createDocumentResponse;
        this.parsingJobRepository = parsingJobRepository;
        this.authentication = authentication;
        this.insuranceSubmissionUpdate = insuranceSubmissionUpdate;
        this.documentCreation = documentCreation;
    }

    @GetMapping("/bills")
    public List<BillResponse> findAllBills(JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        return billRepository.findAllActiveBills(user);
    }

    @GetMapping("/bills/{id}")
    public BillResponse findBillById(@PathVariable UUID id, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        return billRepository.findBillById(id, user)
                .orElseThrow(() -> new NotFoundException("Bill not found"));
    }

    @PostMapping("/bills")
    public void createBill(@RequestParam("file") MultipartFile multipartFile, JwtAuthenticationToken token) {
        try {
            User user = authentication.getCurrentUserFromToken(token);
            String filename = UUID.randomUUID() + ".pdf";
            Path destination = Paths.get(appConfig.getDocumentsDirectory()).resolve(filename);
            multipartFile.transferTo(destination);
            Bill bill = createEmptyBill(user);
            documentCreation.create(filename, bill, user);
            ParsingJob parsingJob = createParsingJob(bill);
            billParsingService.parseAndUpdateBill(bill, destination.toFile(), parsingJob);
        } catch (IOException | RuntimeException e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException("Error while uploading file");
        }
    }

    @PostMapping("/bills/{id}")
    public void updateBill(@PathVariable UUID id, @RequestBody Bill updatedBill, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bill not found"));
        AccessManager.checkIfUserCanAccessBillOrThrowException(user, bill);
        billUpdate.update(bill, updatedBill);
        if (bill.getSubmission() != null) {
            insuranceSubmissionUpdate.updateStatus(bill.getSubmission());
        }
    }

    @DeleteMapping("/bills/{id}")
    public void deleteBill(@PathVariable UUID id, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bill not found"));
        AccessManager.checkIfUserCanAccessBillOrThrowException(user, bill);
        billDeletion.delete(bill);
    }

    @PostMapping("/bills/{id}/documents")
    public void uploadBillDocument(@PathVariable UUID id, @RequestParam("file") MultipartFile multipartFile, JwtAuthenticationToken token) {
        try {
            User user = authentication.getCurrentUserFromToken(token);
            Bill bill = billRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Bill not found"));
            AccessManager.checkIfUserCanAccessBillOrThrowException(user, bill);
            String filename = UUID.randomUUID() + ".pdf";
            Path destination = Paths.get(appConfig.getDocumentsDirectory()).resolve(filename);
            multipartFile.transferTo(destination);
            documentCreation.create(filename, bill, user);
        } catch (IOException | RuntimeException e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException("Error while uploading file");
        }
    }

    @GetMapping("/bills/{id}/documents")
    public List<DocumentResponse> getBillDocuments(@PathVariable UUID id, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bill not found"));
        AccessManager.checkIfUserCanAccessBillOrThrowException(user, bill);
        return documentRepository.findByBillIdAndActiveTrue(id)
                .stream()
                .map(createDocumentResponse::create)
                .toList();
    }

    @GetMapping("/bills/providers")
    public List<String> getProvidersStartingWith(@RequestParam("value") String value, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        return billRepository.findAllProvidersMatchingValue(value, user);
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

    private ParsingJob createParsingJob(Bill bill) {
        ParsingJob parsingJob = new ParsingJob();
        parsingJob.setDateTime(OffsetDateTime.now());
        parsingJob.setStatus(ParsingJob.Status.IN_PROGRESS);
        parsingJob.setBill(bill);
        parsingJobRepository.save(parsingJob);
        return parsingJob;
    }
}