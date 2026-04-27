package com.billkeeper.billkeeperbackend.beneficiary.api;

import com.billkeeper.billkeeperbackend.beneficiary.BeneficiaryService;
import com.billkeeper.billkeeperbackend.beneficiary.api.model.BeneficiaryResponse;
import com.billkeeper.billkeeperbackend.beneficiary.api.model.CreateUpdateBeneficiaryRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    public BeneficiaryController(BeneficiaryService beneficiaryService) {
        this.beneficiaryService = beneficiaryService;
    }

    @GetMapping("/beneficiaries")
    public List<BeneficiaryResponse> getAllBeneficiaries() {
        return beneficiaryService.getAllBeneficiaries();
    }

    @PostMapping("/beneficiaries")
    public void createBeneficiary(@Valid @RequestBody CreateUpdateBeneficiaryRequest request) {
        beneficiaryService.createBeneficiary(request);
    }

    @PostMapping("/beneficiaries/{id}")
    public void updateBeneficiary(@PathVariable UUID id, @RequestBody CreateUpdateBeneficiaryRequest request) {
        beneficiaryService.updateBeneficiary(id, request);
    }

    @DeleteMapping("/beneficiaries/{id}")
    public void deleteBeneficiary(@PathVariable UUID id) {
        beneficiaryService.deleteBeneficiary(id);
    }
}
