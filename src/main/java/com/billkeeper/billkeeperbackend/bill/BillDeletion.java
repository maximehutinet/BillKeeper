package com.billkeeper.billkeeperbackend.bill;

import com.billkeeper.billkeeperbackend.bill.persistence.BillRepository;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.document.persistence.DocumentRepository;
import com.billkeeper.billkeeperbackend.document.persistence.model.Document;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillDeletion {

    private final BillRepository billRepository;
    private final DocumentRepository documentRepository;

    public BillDeletion(BillRepository billRepository, DocumentRepository documentRepository) {
        this.billRepository = billRepository;
        this.documentRepository = documentRepository;
    }

    public void delete(Bill bill) {
        List<Document> documents = documentRepository.findByBillIdAndActive(bill.getId(), true);
        for (Document document : documents) {
            document.setActive(false);
            documentRepository.save(document);
        }
        bill.setActive(false);
        billRepository.save(bill);
    }
}
