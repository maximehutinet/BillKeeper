package com.billkeeper.billkeeperbackend.bill.api;

import com.billkeeper.billkeeperbackend.bill.BillService;
import com.billkeeper.billkeeperbackend.bill.api.model.*;
import com.billkeeper.billkeeperbackend.bill.persistence.model.Bill;
import com.billkeeper.billkeeperbackend.document.DocumentService;
import com.billkeeper.billkeeperbackend.document.api.model.DocumentResponse;
import com.billkeeper.billkeeperbackend.user.persistence.model.User;
import com.billkeeper.billkeeperbackend.utils.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
public class BillController {

    private final BillService billService;
    private final Authentication authentication;
    private final DocumentService documentService;

    public BillController(BillService billService, Authentication authentication, DocumentService documentService) {
        this.billService = billService;
        this.authentication = authentication;
        this.documentService = documentService;
    }

    @GetMapping("/bills")
    public List<BillResponse> findAllBills(JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        return billService.findAllBills(user);
    }

    @GetMapping("/bills/{id}")
    public BillResponse findBillById(@PathVariable UUID id, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        return billService.findBillById(id, user);
    }

    @PostMapping("/bills")
    public void createBill(@RequestParam("file") MultipartFile multipartFile, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        billService.createBill(multipartFile, user);
    }

    @PostMapping("/bills/{id}")
    public void updateBill(@PathVariable UUID id, @RequestBody UpdateBillRequest request, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        Bill bill = billService.getBillForUser(id, user);
        billService.update(bill, request);
    }

    @DeleteMapping("/bills/{id}")
    public void deleteBill(@PathVariable UUID id, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        Bill bill = billService.getBillForUser(id, user);
        billService.delete(bill);
    }

    @PostMapping("/bills/{id}/payment")
    public void updateBillPayment(@PathVariable UUID id, @RequestBody UpdateBillPaymentRequest request, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        Bill bill = billService.getBillForUser(id, user);
        billService.updatePayment(bill, request.getPaidDateTime());
    }

    @PostMapping("/bills/{id}/reimbursement")
    public void updateBillReimbursement(@PathVariable UUID id, @RequestBody UpdateBillReimbursementRequest request, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        Bill bill = billService.getBillForUser(id, user);
        billService.updateReimbursement(bill, request);
    }

    @PostMapping("/bills/{id}/status")
    public void updateBillStatus(@PathVariable UUID id, @RequestBody UpdateBillStatusRequest request, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        Bill bill = billService.getBillForUser(id, user);
        billService.updateStatus(bill, request.getStatus());
    }

    @PostMapping("/bills/{id}/documents")
    public void uploadBillDocument(@PathVariable UUID id, @RequestParam("file") MultipartFile multipartFile, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        Bill bill = billService.getBillForUser(id, user);
        documentService.uploadForBill(bill, multipartFile, user);
    }

    @GetMapping("/bills/{id}/documents")
    public List<DocumentResponse> getBillDocuments(@PathVariable UUID id, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        Bill bill = billService.getBillForUser(id, user);
        return documentService.getDocumentsForBill(bill);
    }

    @GetMapping("/bills/providers")
    public List<String> getProvidersStartingWith(@RequestParam("value") String value, JwtAuthenticationToken token) {
        User user = authentication.getCurrentUserFromToken(token);
        return billService.findAllProvidersMatchingValue(value, user);
    }
}