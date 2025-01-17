package com.billkeeper.billkeeperbackend.beneficiary;

import com.billkeeper.billkeeperbackend.beneficiary.model.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, UUID> {
}