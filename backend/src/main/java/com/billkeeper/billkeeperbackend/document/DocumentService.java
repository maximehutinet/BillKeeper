package com.billkeeper.billkeeperbackend.document;

import com.billkeeper.billkeeperbackend.AppConfig;
import com.billkeeper.billkeeperbackend.bill.persistence.BillRepository;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.document.api.model.CreateDocumentResponse;
import com.billkeeper.billkeeperbackend.document.api.model.DocumentResponse;
import com.billkeeper.billkeeperbackend.document.api.model.UpdateDocumentRequest;
import com.billkeeper.billkeeperbackend.document.persistence.DocumentRepository;
import com.billkeeper.billkeeperbackend.document.persistence.model.Document;
import com.billkeeper.billkeeperbackend.exception.BadRequestException;
import com.billkeeper.billkeeperbackend.exception.InternalServerErrorException;
import com.billkeeper.billkeeperbackend.exception.NotFoundException;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import com.billkeeper.billkeeperbackend.utils.pdf.PDFMerger;
import com.billkeeper.billkeeperbackend.utils.security.accessmanager.AccessManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    private final BillRepository billRepository;
    private final DocumentRepository documentRepository;
    private final CreateDocumentResponse createDocumentResponse;
    private final AppConfig appConfig;
    private final Logger logger = LoggerFactory.getLogger(DocumentService.class);
    private final AccessManager accessManager;

    public DocumentService(BillRepository billRepository, DocumentRepository documentRepository, CreateDocumentResponse createDocumentResponse, AppConfig appConfig, AccessManager accessManager) {
        this.billRepository = billRepository;
        this.documentRepository = documentRepository;
        this.createDocumentResponse = createDocumentResponse;
        this.appConfig = appConfig;
        this.accessManager = accessManager;
    }

    @Transactional
    public void createDocument(MultipartFile multipartFile, User user) {
        try {
            String filename = UUID.randomUUID() + ".pdf";
            Path destination = Paths.get(appConfig.getDocumentsDirectory()).resolve(filename);
            multipartFile.transferTo(destination);
            create(filename, null, user);
        } catch (IOException | RuntimeException e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException("Error while uploading file");
        }
    }

    @Transactional
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

    public ResponseEntity<Resource> getDocument(UUID id, User user) {
        try {
            Document document = getDocumentForUser(id, user);
            File file = new File(appConfig.getDocumentsDirectory() + File.separator + document.getName());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/pdf"))
                    .body(new UrlResource(file.toURI()));
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException("Error while fetching document");
        }
    }

    private Document getDocumentForUser(UUID id, User user) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document not found"));
        accessManager.checkIfUserCanAccessDocumentOrThrowException(user, document);
        return document;
    }

    public ResponseEntity<Resource> getMergedDocuments(List<UUID> billIds, User user) {
        try {
            List<Document> documents = new ArrayList<>();
            billIds.forEach(id -> documents.addAll(documentRepository.findByBillIdAndActiveTrue(id, user)));
            if (documents.isEmpty()) {
                throw new BadRequestException("No documents found");
            }
            List<File> files = documents
                    .stream()
                    .map(document -> new File(appConfig.getDocumentsDirectory() + File.separator + document.getName()))
                    .toList();
            File mergedFile = PDFMerger.mergeFiles(files);
            mergedFile.deleteOnExit();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/pdf"))
                    .body(new UrlResource(mergedFile.toURI()));
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException("Error while merging documents");
        }
    }

    @Transactional
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

    @Transactional
    public void updateDocument(UUID id, UpdateDocumentRequest request, User user) {
        Document document = getDocumentForUser(id, user);
        if (request.getDescription() != null) {
            document.setDescription(request.getDescription());
        }
        if (request.getBillId() != null) {
            Bill bill = billRepository.findById(request.getBillId())
                    .orElseThrow(() -> new NotFoundException("Bill not found"));
            document.setBill(bill);
        }
        documentRepository.save(document);
    }

    @Transactional
    public void deleteDocument(UUID id, User user) {
        Document document = getDocumentForUser(id, user);
        document.setActive(false);
        documentRepository.save(document);
    }

    @Transactional
    public void deactivateForBill(UUID billId) {
        documentRepository.deactivateByBillId(billId);
    }


}
