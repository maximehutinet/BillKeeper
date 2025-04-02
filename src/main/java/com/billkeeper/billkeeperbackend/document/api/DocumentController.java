package com.billkeeper.billkeeperbackend.document.api;

import com.billkeeper.billkeeperbackend.AppConfig;
import com.billkeeper.billkeeperbackend.bill.persistence.BillRepository;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.document.api.model.DocumentResponse;
import com.billkeeper.billkeeperbackend.document.api.model.UpdateDocumentRequest;
import com.billkeeper.billkeeperbackend.document.persistence.DocumentRepository;
import com.billkeeper.billkeeperbackend.document.persistence.model.Document;
import com.billkeeper.billkeeperbackend.exception.BadRequestException;
import com.billkeeper.billkeeperbackend.exception.InternalServerErrorException;
import com.billkeeper.billkeeperbackend.exception.NotFoundException;
import com.billkeeper.billkeeperbackend.utils.PDFMerger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class DocumentController {

    private final DocumentRepository documentRepository;
    private final BillRepository billRepository;
    private final AppConfig appConfig;
    private final CreateDocumentResponse createDocumentResponse;
    private final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    public DocumentController(DocumentRepository documentRepository, BillRepository billRepository, AppConfig appConfig, CreateDocumentResponse createDocumentResponse) {
        this.documentRepository = documentRepository;
        this.billRepository = billRepository;
        this.appConfig = appConfig;
        this.createDocumentResponse = createDocumentResponse;
    }

    @PostMapping("/documents")
    public void createDocument(@RequestParam("file") MultipartFile multipartFile) {
        try {
            String filename = UUID.randomUUID() + ".pdf";
            Path destination = Paths.get(appConfig.getDocumentsDirectory()).resolve(filename);
            multipartFile.transferTo(destination);
            Document document = new Document();
            document.setActive(true);
            document.setDateTime(OffsetDateTime.now());
            document.setName(filename);
            documentRepository.save(document);
        } catch (IOException | RuntimeException e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException("Error while uploading file");
        }
    }

    @GetMapping("/documents")
    public ResponseEntity<Resource> getMergedBillsDocuments(@RequestParam("billIds") List<UUID> billIds) {
        try {
            List<Document> documents = new ArrayList<>();
            billIds.forEach(id -> documents.addAll(documentRepository.findByBillIdAndActiveTrue(id)));
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
            throw new InternalServerErrorException("Error while merges the bills");
        }
    }

    @GetMapping("/documents/{id}")
    public ResponseEntity<Resource> getDocument(@PathVariable("id") UUID id) {
        try {
            Document document = documentRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Document not found"));
            File file = new File(appConfig.getDocumentsDirectory() + File.separator + document.getName());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/pdf"))
                    .body(new UrlResource(file.toURI()));
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException("Error while fetching document");
        }
    }

    @PostMapping("/documents/{id}")
    public void updateDocument(@RequestBody UpdateDocumentRequest request, @PathVariable("id") UUID id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document not found"));
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

    @DeleteMapping("/documents/{id}")
    public void delete(@PathVariable("id") UUID id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document not found"));
        document.setActive(false);
        documentRepository.save(document);
    }

    @GetMapping("/documents/orphans")
    public List<DocumentResponse> getAllOrphansDocuments() {
        return documentRepository.findByBillIdNullAndActiveTrue()
                .stream()
                .map(createDocumentResponse::create)
                .toList();
    }
}
