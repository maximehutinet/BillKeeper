package com.billkeeper.billkeeperbackend.utils.parsing;

import com.billkeeper.billkeeperbackend.beneficiary.persistence.model.Beneficiary;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CHFBillParser implements BillParser {

    private final String text;
    private final List<Beneficiary> registeredBeneficiaries;
    private static final Pattern NAME_PATTERN = Pattern.compile("Auteur N° GLN \\(B\\)\\s*\\d+(.*)");
    private static final Pattern CHF_AMOUNT_PATTERN = Pattern.compile("(CHF)\\s?\\d+\\s?'?\\d+\\.?\\d+");
    private static final Pattern TOTAL_PATTERN = Pattern.compile("total:\\s*\\d+\\.\\d");

    public CHFBillParser(String text, List<Beneficiary> registeredBeneficiaries) {
        this.text = text;
        this.registeredBeneficiaries = registeredBeneficiaries;
    }

    @Override
    public Optional<String> getBillName() {
        Set<String> matches = new HashSet<>();
        Matcher matcher = NAME_PATTERN.matcher(text);
        while (matcher.find()) {
            matches.add(matcher.group(1));
        }
        return matches
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Double> getBillAmount() {
        List<Pattern> patterns = List.of(CHF_AMOUNT_PATTERN, TOTAL_PATTERN);
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return Optional.of(Double.valueOf(matcher.group().replaceAll("[^\\d.]+", "")));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Beneficiary> getBillBeneficiary() {
        for (Beneficiary beneficiary : registeredBeneficiaries) {
            if (text.contains(beneficiary.getFirstname())) {
                return Optional.of(beneficiary);
            }
        }
        return Optional.empty();
    }
}
