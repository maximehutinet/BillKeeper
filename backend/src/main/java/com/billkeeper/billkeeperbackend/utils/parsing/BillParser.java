package com.billkeeper.billkeeperbackend.utils.parsing;

import com.billkeeper.billkeeperbackend.beneficiary.persistence.model.Beneficiary;

import java.util.Optional;

public interface BillParser {
    Optional<String> getBillName();
    Optional<Double> getBillAmount();
    Optional<Beneficiary> getBillBeneficiary();
}
