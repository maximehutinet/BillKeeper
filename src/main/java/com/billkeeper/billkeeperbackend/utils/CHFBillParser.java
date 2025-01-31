package com.billkeeper.billkeeperbackend.utils;

import com.billkeeper.billkeeperbackend.beneficiary.persistence.model.Beneficiary;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CHFBillParser implements BillParser {

    private final String text;
    private final List<Beneficiary> registeredBeneficiaries;

    public CHFBillParser(String text, List<Beneficiary> registeredBeneficiaries) {
        this.text = text;
        this.registeredBeneficiaries = registeredBeneficiaries;
    }

    @Override
    public Optional<String> getBillName() {
        Set<String> matches = new HashSet<>();
        String regex = "Auteur N° GLN \\(B\\)\\s*\\d+(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            matches.add(matcher.group(1));
        }
        return matches
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Double> getBillAmount() {
        Set<String> matches = new HashSet<>();
        List<String> regexs = new ArrayList<>();
        // Regex matching CHF 2'480.95 for example
        regexs.add("(CHF)\\s?\\d+\\s?'?\\d+\\.?\\d+");
        // Regex matching total: 117.00 for example
        regexs.add("total:\\s*\\d+\\.\\d");
        regexs.forEach(regex -> {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                String value = matcher.group();
                matches.add(value);
            }
        });

        // Regex matching CHF for example
        if (!matches.isEmpty()) {
            return matches
                    .stream()
                    .findFirst()
                    .map(value -> Double.valueOf(value.replaceAll("[^\\d\\.']+", "")));
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
