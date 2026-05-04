package com.billkeeper.billkeeperbackend.beneficiary;

import com.billkeeper.billkeeperbackend.beneficiary.api.model.BeneficiaryResponse;
import com.billkeeper.billkeeperbackend.beneficiary.api.model.CreateUpdateBeneficiaryRequest;
import com.billkeeper.billkeeperbackend.beneficiary.persistence.BeneficiaryRepository;
import com.billkeeper.billkeeperbackend.beneficiary.persistence.model.Beneficiary;
import com.billkeeper.billkeeperbackend.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BeneficiaryService {
    private final BeneficiaryRepository beneficiaryRepository;

    public BeneficiaryService(BeneficiaryRepository beneficiaryRepository) {
        this.beneficiaryRepository = beneficiaryRepository;
    }

    public List<BeneficiaryResponse> getAllBeneficiaries() {
        return beneficiaryRepository.findAllByActiveTrue()
                .stream()
                .map(this::buildBeneficiaryResponse)
                .toList();
    }

    private BeneficiaryResponse buildBeneficiaryResponse(Beneficiary beneficiary) {
        return BeneficiaryResponse.builder()
                .id(beneficiary.getId())
                .active(beneficiary.getActive())
                .firstname(beneficiary.getFirstname())
                .build();
    }

    public void createBeneficiary(CreateUpdateBeneficiaryRequest request) {
        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setActive(true);
        beneficiary.setFirstname(request.getFirstname());
        beneficiaryRepository.save(beneficiary);
    }

    public void updateBeneficiary(UUID id, CreateUpdateBeneficiaryRequest request) {
        Beneficiary beneficiary = beneficiaryRepository.findByIdAndActiveTrue(id).
                orElseThrow(() -> new NotFoundException("Beneficiary not found"));
        if (request.getFirstname() != null && !beneficiary.getFirstname().equals(request.getFirstname())) {
            beneficiary.setFirstname(request.getFirstname());
            beneficiaryRepository.save(beneficiary);
        }
    }

    public void deleteBeneficiary(UUID id) {
        Beneficiary beneficiary = beneficiaryRepository.findByIdAndActiveTrue(id).
                orElseThrow(() -> new NotFoundException("Beneficiary not found"));
        beneficiary.setActive(false);
        beneficiaryRepository.save(beneficiary);
    }
}
