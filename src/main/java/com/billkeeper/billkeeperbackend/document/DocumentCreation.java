package com.billkeeper.billkeeperbackend.document;

import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.document.persistence.DocumentRepository;
import com.billkeeper.billkeeperbackend.document.persistence.model.Document;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class DocumentCreation {

    private final DocumentRepository documentRepository;

    public DocumentCreation(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
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
}
