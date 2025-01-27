package com.billkeeper.billkeeperbackend.bill.api;

import com.billkeeper.billkeeperbackend.AppConfig;
import com.billkeeper.billkeeperbackend.beneficiary.BeneficiaryRepository;
import com.billkeeper.billkeeperbackend.beneficiary.model.Beneficiary;
import com.billkeeper.billkeeperbackend.bill.persistence.BillRepository;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.utils.BillParser;
import com.billkeeper.billkeeperbackend.utils.CHFBillParser;
import com.billkeeper.billkeeperbackend.utils.EuroBillParser;
import com.billkeeper.billkeeperbackend.document.persistence.DocumentRepository;
import com.billkeeper.billkeeperbackend.document.persistence.model.Document;
import com.billkeeper.billkeeperbackend.exception.InternalServerErrorException;
import com.billkeeper.billkeeperbackend.exception.NotFoundException;
import com.billkeeper.billkeeperbackend.utils.PDFParser;
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
    private final AppConfig appConfig;
    private final PDFParser pdfParser;
    private final BeneficiaryRepository beneficiaryRepository;

    public BillController(BillRepository billRepository, DocumentRepository documentRepository, AppConfig appConfig, PDFParser pdfParser, BeneficiaryRepository beneficiaryRepository) {
        this.billRepository = billRepository;
        this.documentRepository = documentRepository;
        this.appConfig = appConfig;
        this.pdfParser = pdfParser;
        this.beneficiaryRepository = beneficiaryRepository;
    }

    @GetMapping("/bills")
    public Iterable<Bill> findAllBills() {
        return billRepository.findAllByOrderByDateTimeDesc();
    }

    @GetMapping("/bills/{id}")
    public Bill findBillById(@PathVariable UUID id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bill not found"));
    }

    @PostMapping("/bills")
    public Bill createBill(@RequestBody Bill bill) {
        bill.setActive(true);
        bill.setDateTime(OffsetDateTime.now());
        bill.setStatus(Bill.Status.TO_FILE);
        return billRepository.save(bill);
    }

    @PostMapping("/bills/{id}/document")
    public void uploadBillDocument(@PathVariable UUID id, @RequestParam("file") MultipartFile multipartFile) {
        try {
            Bill bill = billRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Bill not found"));
            String filename = UUID.randomUUID() + ".pdf";
            Path destination = Paths.get(appConfig.getDocumentsDirectory()).resolve(filename);
            multipartFile.transferTo(destination);
            createDocument(filename, bill);
            String text = pdfParser.extractTextFromFile(destination.toFile());
            updateBillValues(bill, text);
        } catch (IOException | RuntimeException e) {
            throw new InternalServerErrorException("Error while uploading file");
        }
    }

    private void createDocument(String fileName, Bill bill) {
        Document document = new Document();
        document.setActive(true);
        document.setDateTime(OffsetDateTime.now());
        document.setName(fileName);
        document.setBill(bill);
        documentRepository.save(document);
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
        if (text.contains("Justificatif de remboursement") || text.contains("CHF") || text.contains("Payable par")) {
            return Bill.Currency.CHF;
        }
        return Bill.Currency.EURO;
    }

}