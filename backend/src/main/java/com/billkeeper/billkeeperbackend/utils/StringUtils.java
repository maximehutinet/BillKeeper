package com.billkeeper.billkeeperbackend.utils;

import java.text.Normalizer;

public class StringUtils {

    public static String removeAccents(String text) {
        String normalizedString = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalizedString.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public static String cleanStringForComparison(String value) {
        return removeAccents(value.toLowerCase());
    }
}