package com.billkeeper.billkeeperbackend.beneficiary;

import com.billkeeper.billkeeperbackend.beneficiary.persistence.model.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, UUID> {

    List<Beneficiary> findAllByActiveTrue();
}