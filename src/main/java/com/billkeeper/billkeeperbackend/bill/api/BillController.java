package com.billkeeper.billkeeperbackend.bill.api;

import com.billkeeper.billkeeperbackend.AppConfig;
import com.billkeeper.billkeeperbackend.bill.persistence.BillRepository;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.document.persistence.DocumentRepository;
import com.billkeeper.billkeeperbackend.document.persistence.model.Document;
import com.billkeeper.billkeeperbackend.exception.InternalServerErrorException;
import com.billkeeper.billkeeperbackend.exception.NotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
public class BillController {

    private final BillRepository billRepository;
    private final DocumentRepository documentRepository;
    private final AppConfig appConfig;

    public BillController(BillRepository billRepository, DocumentRepository documentRepository, AppConfig appConfig) {
        this.billRepository = billRepository;
        this.documentRepository = documentRepository;
        this.appConfig = appConfig;
    }

    @GetMapping("/bills")
    public Iterable<Bill> findAllBills() {
        return billRepository.findAll();
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
    public void uploadBillDocument(@PathVariable UUID id, @RequestParam("file") MultipartFile file) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bill not found"));
        String fileName = UUID.randomUUID() + ".pdf";
        try {
            file.transferTo(Paths.get(appConfig.getDocumentsDirectory()).resolve(fileName));
            Document document = new Document();
            document.setActive(true);
            document.setDateTime(OffsetDateTime.now());
            document.setName(fileName);
            document.setBill(bill);
            documentRepository.save(document);
        } catch (IOException e) {
            throw new InternalServerErrorException("Error while uploading file");
        }
    }
}