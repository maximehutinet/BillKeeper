package com.billkeeper.billkeeperbackend.bill.api;

import com.billkeeper.billkeeperbackend.bill.persistence.BillRepository;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.exception.NotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.UUID;

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
}