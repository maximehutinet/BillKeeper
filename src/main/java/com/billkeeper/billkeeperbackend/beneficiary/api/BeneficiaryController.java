package com.billkeeper.billkeeperbackend.beneficiary.api;

import com.billkeeper.billkeeperbackend.beneficiary.BeneficiaryRepository;
import com.billkeeper.billkeeperbackend.beneficiary.api.model.CreateUpdateBeneficiaryRequest;
import com.billkeeper.billkeeperbackend.beneficiary.persistence.model.Beneficiary;
import com.billkeeper.billkeeperbackend.exception.BadRequestException;
import com.billkeeper.billkeeperbackend.exception.NotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class BeneficiaryController {

    private final BeneficiaryRepository beneficiaryRepository;

    public BeneficiaryController(BeneficiaryRepository beneficiaryRepository) {
        this.beneficiaryRepository = beneficiaryRepository;
    }

    @GetMapping("/beneficiaries")
    public List<Beneficiary> getAllBeneficiaries() {
        return beneficiaryRepository.findAllByActiveTrue();
    }

    @PostMapping("/beneficiaries")
    public void createBeneficiary(@RequestBody CreateUpdateBeneficiaryRequest request) {
        if (request.getFirstname() == null || request.getFirstname().isEmpty()) {
            throw new BadRequestException("Firstname cannot be empty");
        }
        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setActive(true);
        beneficiary.setFirstname(request.getFirstname());
        beneficiaryRepository.save(beneficiary);
    }

    @PostMapping("/beneficiaries/{id}")
    public void updateBeneficiary(@PathVariable UUID id, @RequestBody CreateUpdateBeneficiaryRequest request) {
        Beneficiary beneficiary = beneficiaryRepository.findByIdAndActiveTrue(id).
                orElseThrow(() -> new NotFoundException("Beneficiary not found"));
        if (request.getFirstname() != null && !beneficiary.getFirstname().equals(request.getFirstname())) {
            beneficiary.setFirstname(request.getFirstname());
            beneficiaryRepository.save(beneficiary);
        }
    }

    @DeleteMapping("/beneficiaries/{id}")
    public void deleteBeneficiary(@PathVariable UUID id) {
        Beneficiary beneficiary = beneficiaryRepository.findByIdAndActiveTrue(id).
                orElseThrow(() -> new NotFoundException("Beneficiary not found"));
        beneficiary.setActive(false);
        beneficiaryRepository.save(beneficiary);
    }
}
