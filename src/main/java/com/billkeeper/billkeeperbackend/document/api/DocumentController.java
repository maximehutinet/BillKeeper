package com.billkeeper.billkeeperbackend.document.api;

import com.billkeeper.billkeeperbackend.AppConfig;
import com.billkeeper.billkeeperbackend.document.persistence.DocumentRepository;
import com.billkeeper.billkeeperbackend.document.persistence.model.Document;
import com.billkeeper.billkeeperbackend.exception.InternalServerErrorException;
import com.billkeeper.billkeeperbackend.exception.NotFoundException;
import com.billkeeper.billkeeperbackend.utils.PDFMerger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class DocumentController {

    private DocumentRepository documentRepository;
    private final AppConfig appConfig;

    public DocumentController(DocumentRepository documentRepository, AppConfig appConfig) {
        this.documentRepository = documentRepository;
        this.appConfig = appConfig;
    }

    @GetMapping("/documents/bills/{ids}")
    public ResponseEntity<Resource> getMergedBillsDocuments(@PathVariable("ids") List<UUID> ids) {
        try {
            List<Document> documents = new ArrayList<>();
            ids.forEach(id -> {
                documents.addAll(documentRepository.findByBillId(id));
            });
            documents.forEach(document -> {
                System.out.println("document.getName() = " + document.getName());
            });
            List<File> files = documents
                    .stream()
                    .map(document -> {
                        return new File(appConfig.getDocumentsDirectory() + File.separator + document.getName());
                    }).toList();
            File mergedFile = PDFMerger.mergeFiles(files);
            mergedFile.deleteOnExit();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/pdf"))
                    .body(new UrlResource(mergedFile.toURI()));
        } catch (IOException e) {
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
            throw new InternalServerErrorException("Error while fetching document");
        }
    }

    @DeleteMapping("/documents/{id}")
    public void delete(@PathVariable("id") UUID id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document not found"));
        document.setActive(false);
        documentRepository.save(document);
    }
}
