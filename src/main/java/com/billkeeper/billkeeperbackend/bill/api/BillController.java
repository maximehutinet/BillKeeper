package com.billkeeper.billkeeperbackend.bill.api;

import com.billkeeper.billkeeperbackend.AppConfig;
import com.billkeeper.billkeeperbackend.beneficiary.BeneficiaryRepository;
import com.billkeeper.billkeeperbackend.beneficiary.persistence.model.Beneficiary;
import com.billkeeper.billkeeperbackend.bill.BillDeletion;
import com.billkeeper.billkeeperbackend.bill.BillUpdate;
import com.billkeeper.billkeeperbackend.bill.persistence.BillRepository;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.document.api.CreateDocumentResponse;
import com.billkeeper.billkeeperbackend.document.api.model.DocumentResponse;
import com.billkeeper.billkeeperbackend.document.persistence.DocumentRepository;
import com.billkeeper.billkeeperbackend.document.persistence.model.Document;
import com.billkeeper.billkeeperbackend.exception.InternalServerErrorException;
import com.billkeeper.billkeeperbackend.exception.NotFoundException;
import com.billkeeper.billkeeperbackend.user.persistence.UserRepository;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import com.billkeeper.billkeeperbackend.utils.BillParser;
import com.billkeeper.billkeeperbackend.utils.CHFBillParser;
import com.billkeeper.billkeeperbackend.utils.EuroBillParser;
import com.billkeeper.billkeeperbackend.utils.PDFParser;
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
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final AppConfig appConfig;
    private final PDFParser pdfParser;
    private final BeneficiaryRepository beneficiaryRepository;
    private final BillUpdate billUpdate;
    private final BillDeletion billDeletion;
    private final CreateDocumentResponse createDocumentResponse;

    public BillController(BillRepository billRepository, DocumentRepository documentRepository, UserRepository userRepository, AppConfig appConfig, PDFParser pdfParser, BeneficiaryRepository beneficiaryRepository, BillUpdate billUpdate, BillDeletion billDeletion, CreateDocumentResponse createDocumentResponse) {
        this.billRepository = billRepository;
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.appConfig = appConfig;
        this.pdfParser = pdfParser;
        this.beneficiaryRepository = beneficiaryRepository;
        this.billUpdate = billUpdate;
        this.billDeletion = billDeletion;
        this.createDocumentResponse = createDocumentResponse;
    }

    @GetMapping("/bills")
    public Iterable<Bill> findAllBills() {
        return billRepository.findAllByActiveTrueOrderByDateTimeDesc();
    }

    @GetMapping("/bills/{id}")
    public Bill findBillById(@PathVariable UUID id) {
        return billRepository.findById(id)
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
            parseAndUpdateBill(bill, destination);
        } catch (IOException | RuntimeException e) {
            throw new InternalServerErrorException("Error while uploading file");
        }
    }

    @PostMapping("/bills/{id}")
    public void updateBill(@PathVariable UUID id, @RequestBody Bill updatedBill) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bill not found"));
        billUpdate.update(bill, updatedBill);
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

    private void parseAndUpdateBill(Bill bill, Path destination) {
        try {
            String text = pdfParser.extractTextFromFile(destination.toFile());
            updateBillValues(bill, text);
        } catch (IOException ignored) { }
    }

    private void updateBillValues(Bill bill, String text) {
        List<Beneficiary> registeredBeneficiaries = beneficiaryRepository.findAll();
        Bill.Currency currency = getBillCurrency(text);
        BillParser billParser = currency == Bill.Currency.CHF ? new CHFBillParser(text, registeredBeneficiaries) : new EuroBillParser(text, registeredBeneficiaries);
        bill.setCurrency(currency);
        billParser.getBillName().ifPresent(bill::setName);
        billParser.getBillAmount().ifPresent(bill::setAmount);
        billParser.getBillBeneficiary().ifPresent(bill::setBeneficiary);
        billRepository.save(bill);
    }

    private Bill.Currency getBillCurrency(String text) {
        if (text.contains("EURO") || text.contains("EUR") || text.contains("€")) {
            return Bill.Currency.EUR;
        }
        return Bill.Currency.CHF;
    }

}