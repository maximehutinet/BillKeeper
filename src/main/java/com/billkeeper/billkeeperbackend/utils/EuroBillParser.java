package com.billkeeper.billkeeperbackend.utils;

import com.billkeeper.billkeeperbackend.beneficiary.model.Beneficiary;

import java.util.List;
import java.util.Optional;

public class EuroBillParser implements BillParser {

    private String text;
    private List<Beneficiary> registeredBeneficiaries;

    public EuroBillParser(String text, List<Beneficiary> registeredBeneficiaries) {
        this.text = text;
        this.registeredBeneficiaries = registeredBeneficiaries;
    }

    @Override
    public Optional<String> getBillName() {
        return Optional.empty();
    }

    @Override
    public Optional<Double> getBillAmount() {
        return Optional.empty();
    }

    @Override
    public Optional<Beneficiary> getBillBeneficiary() {
        return Optional.empty();
    }
}