package com.billkeeper.billkeeperbackend.bill.api;

import com.billkeeper.billkeeperbackend.AppConfig;
import com.billkeeper.billkeeperbackend.bill.BillDeletion;
import com.billkeeper.billkeeperbackend.bill.BillUpdate;
import com.billkeeper.billkeeperbackend.bill.api.model.BillResponse;
import com.billkeeper.billkeeperbackend.bill.persistence.BillRepository;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.document.api.CreateDocumentResponse;
import com.billkeeper.billkeeperbackend.document.api.model.DocumentResponse;
import com.billkeeper.billkeeperbackend.document.persistence.DocumentRepository;
import com.billkeeper.billkeeperbackend.document.persistence.model.Document;
import com.billkeeper.billkeeperbackend.exception.InternalServerErrorException;
import com.billkeeper.billkeeperbackend.exception.NotFoundException;
import com.billkeeper.billkeeperbackend.parsingjob.persistence.ParsingJobRepository;
import com.billkeeper.billkeeperbackend.parsingjob.persistence.model.ParsingJob;
import com.billkeeper.billkeeperbackend.submission.InsuranceSubmissionUpdate;
import com.billkeeper.billkeeperbackend.user.persistence.UserRepository;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import com.billkeeper.billkeeperbackend.utils.BillParsingService;
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
    private final UserRepository userRepository;
    private final AppConfig appConfig;
    private final BillUpdate billUpdate;
    private final BillDeletion billDeletion;
    private final CreateDocumentResponse createDocumentResponse;
    private final Logger logger = LoggerFactory.getLogger(BillController.class);
    private final ParsingJobRepository parsingJobRepository;

    private final InsuranceSubmissionUpdate insuranceSubmissionUpdate;

    public BillController(BillRepository billRepository, DocumentRepository documentRepository, UserRepository userRepository, AppConfig appConfig, BillParsingService billParsingService, BillUpdate billUpdate, BillDeletion billDeletion, CreateDocumentResponse createDocumentResponse, ParsingJobRepository parsingJobRepository, InsuranceSubmissionUpdate insuranceSubmissionUpdate) {
        this.billRepository = billRepository;
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.appConfig = appConfig;
        this.billParsingService = billParsingService;
        this.billUpdate = billUpdate;
        this.billDeletion = billDeletion;
        this.createDocumentResponse = createDocumentResponse;
        this.parsingJobRepository = parsingJobRepository;
        this.insuranceSubmissionUpdate = insuranceSubmissionUpdate;
    }

    @GetMapping("/bills")
    public List<BillResponse> findAllBills() {
        return billRepository.findAllActiveBills();
    }

    @GetMapping("/bills/{id}")
    public BillResponse findBillById(@PathVariable UUID id) {
        return billRepository.findBillById(id)
                .orElseThrow(() -> new NotFoundException("Bill not found"));
    }

    @PostMapping("/bills")
    public void createBill(@RequestParam("file") MultipartFile multipartFile, JwtAuthenticationToken jwtAuthenticationToken) {
        try {
            User user = userRepository.findUserByKeycloakId(jwtAuthenticationToken.getName())
                    .orElse(null);
            String filename = UUID.randomUUID() + ".pdf";
            Path destination = Paths.get(appConfig.getDocumentsDirectory()).resolve(filename);
            multipartFile.transferTo(destination);
            Bill bill = createEmptyBill(user);
            createDocument(filename, bill);
            ParsingJob parsingJob = createParsingJob(bill);
            billParsingService.parseAndUpdateBill(bill, destination.toFile(), parsingJob);
        } catch (IOException | RuntimeException e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException("Error while uploading file");
        }
    }

    @PostMapping("/bills/{id}")
    public void updateBill(@PathVariable UUID id, @RequestBody Bill updatedBill) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bill not found"));
        billUpdate.update(bill, updatedBill);
        if (bill.getSubmission() != null) {
            insuranceSubmissionUpdate.updateStatus(bill.getSubmission());
        }
    }

    @DeleteMapping("/bills/{id}")
    public void deleteBill(@PathVariable UUID id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bill not found"));
        billDeletion.delete(bill);
    }

    @PostMapping("/bills/{id}/documents")
    public void uploadBillDocument(@PathVariable UUID id, @RequestParam("file") MultipartFile multipartFile) {
        try {
            Bill bill = billRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Bill not found"));
            String filename = UUID.randomUUID() + ".pdf";
            Path destination = Paths.get(appConfig.getDocumentsDirectory()).resolve(filename);
            multipartFile.transferTo(destination);
            createDocument(filename, bill);
        } catch (IOException | RuntimeException e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException("Error while uploading file");
        }
    }

    @GetMapping("/bills/{id}/documents")
    public List<DocumentResponse> getBillDocuments(@PathVariable UUID id) {
        if (!billRepository.existsById(id)) {
            throw new NotFoundException("Bill not found");
        }
        return documentRepository.findByBillIdAndActiveTrue(id)
                .stream()
                .map(createDocumentResponse::create)
                .toList();
    }

    @GetMapping("/bills/providers")
    public List<String> getProvidersStartingWith(@RequestParam("value") String value) {
        return billRepository.findAllProvidersMatchingValue(value);
    }

    private Bill createEmptyBill(User user) {
        Bill bill = new Bill();
        bill.setActive(true);
        bill.setDateTime(OffsetDateTime.now());
        bill.setStatus(Bill.Status.TO_FILE);
        bill.setUser(user);
        billRepository.save(bill);
        return bill;
    }

    private void createDocument(String filename, Bill bill) {
        Document document = new Document();
        document.setActive(true);
        document.setDateTime(OffsetDateTime.now());
        document.setName(filename);
        document.setBill(bill);
        documentRepository.save(document);
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