package com.billkeeper.billkeeperbackend.bill.api;

import com.billkeeper.billkeeperbackend.bill.persistence.BillRepository;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
public class BillController {

    private final BillRepository billRepository;

    public BillController(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @GetMapping("/bills")
    public Iterable<Bill> findAllBills() {
        return billRepository.findAll();
    }

    @PostMapping("/bills")
    public Bill createBill(@RequestBody Bill bill) {
        bill.setActive(true);
        bill.setDateTime(OffsetDateTime.now());
        bill.setStatus(Bill.Status.TO_FILE);
        return billRepository.save(bill);
    }
}