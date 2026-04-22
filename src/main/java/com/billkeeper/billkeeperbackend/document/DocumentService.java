package com.billkeeper.billkeeperbackend.document;

import com.billkeeper.billkeeperbackend.AppConfig;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.document.api.CreateDocumentResponse;
import com.billkeeper.billkeeperbackend.document.api.model.DocumentResponse;
import com.billkeeper.billkeeperbackend.document.persistence.DocumentRepository;
import com.billkeeper.billkeeperbackend.document.persistence.model.Document;
import com.billkeeper.billkeeperbackend.exception.InternalServerErrorException;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final CreateDocumentResponse createDocumentResponse;
    private final AppConfig appConfig;
    private final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    public DocumentService(DocumentRepository documentRepository, CreateDocumentResponse createDocumentResponse, AppConfig appConfig) {
        this.documentRepository = documentRepository;
        this.createDocumentResponse = createDocumentResponse;
        this.appConfig = appConfig;
    }

    public void create(String filename, Bill bill, User user) {
        Document document = new Document();
        document.setActive(true);
        document.setDateTime(OffsetDateTime.now());
        document.setName(filename);
        document.setBill(bill);
        document.setUser(user);
        documentRepository.save(document);
    }

    public List<DocumentResponse> getDocumentsForBill(Bill bill) {
        return documentRepository.findByBillIdAndActiveTrue(bill.getId())
                .stream()
                .map(createDocumentResponse::create)
                .toList();
    }

    public void uploadForBill(Bill bill, MultipartFile multipartFile, User user) {
        try {
            String filename = UUID.randomUUID() + ".pdf";
            Path destination = Paths.get(appConfig.getDocumentsDirectory()).resolve(filename);
            multipartFile.transferTo(destination);
            create(filename, bill, user);
        } catch (IOException | RuntimeException e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException("Error while uploading file");
        }
    }

    public void deactivateForBill(UUID billId) {
        documentRepository.deactivateByBillId(billId);
    }
}
