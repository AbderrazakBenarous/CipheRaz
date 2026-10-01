package com.cryptolab.core.cryptanalysis;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Educational cryptanalysis engine for character frequency analysis and language statistics.
 *
 * Developed by Abderrazak BENAROUS
 */
public class FrequencyAnalysis {

    // Standard English letter frequency distribution (percentages)
    public static final double[] ENGLISH_FREQUENCIES = {
            8.167, 1.492, 2.782, 4.253, 12.702, 2.228, 2.015, 6.094, 6.966, 0.153,
            0.772, 4.025, 2.406, 6.749, 7.507, 1.929, 0.095, 5.987, 6.327, 9.056,
            2.758, 0.978, 2.360, 0.150, 1.974, 0.074
    };

    // Standard French letter frequency distribution (percentages)
    public static final double[] FRENCH_FREQUENCIES = {
            7.636, 0.901, 3.260, 3.669, 14.715, 1.066, 0.866, 0.737, 7.529, 0.613,
            0.049, 5.456, 2.968, 7.095, 5.796, 2.521, 1.362, 6.693, 7.948, 7.244,
            6.311, 1.838, 0.074, 0.427, 0.128, 0.326
    };

    public record AnalysisResult(
            int totalLetters,
            int[] letterCounts,
            double[] letterPercentages,
            double indexOfCoincidence,
            double chiSquareEnglish,
            char mostFrequentLetter,
            Map<String, Integer> topDigraphs
    ) {}

    public static AnalysisResult analyze(String text) {
        if (text == null || text.isEmpty()) {
            return new AnalysisResult(0, new int[26], new double[26], 0.0, 0.0, ' ', Collections.emptyMap());
        }

        int[] counts = new int[26];
        int totalLetters = 0;
        char mostFrequent = 'A';
        int maxCount = -1;

        String upper = text.toUpperCase();
        for (int i = 0; i < upper.length(); i++) {
            char c = upper.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                int idx = c - 'A';
                counts[idx]++;
                totalLetters++;
                if (counts[idx] > maxCount) {
                    maxCount = counts[idx];
                    mostFrequent = c;
                }
            }
        }

        double[] percentages = new double[26];
        double sumCoincidence = 0.0;

        if (totalLetters > 0) {
            for (int i = 0; i < 26; i++) {
                percentages[i] = (counts[i] * 100.0) / totalLetters;
                sumCoincidence += (double) counts[i] * (counts[i] - 1);
            }
        }

        double ioc = (totalLetters > 1) ? sumCoincidence / ((double) totalLetters * (totalLetters - 1)) : 0.0;
        double chiSquare = calculateChiSquare(counts, totalLetters, ENGLISH_FREQUENCIES);

        // Analyze top digraphs
        Map<String, Integer> digraphMap = new HashMap<>();
        for (int i = 0; i < upper.length() - 1; i++) {
            char c1 = upper.charAt(i);
            char c2 = upper.charAt(i + 1);
            if (c1 >= 'A' && c1 <= 'Z' && c2 >= 'A' && c2 <= 'Z') {
                String pair = "" + c1 + c2;
                digraphMap.put(pair, digraphMap.getOrDefault(pair, 0) + 1);
            }
        }

        return new AnalysisResult(totalLetters, counts, percentages, ioc, chiSquare, mostFrequent, digraphMap);
    }

    public static double calculateChiSquare(int[] observedCounts, int totalLetters, double[] expectedPercentages) {
        if (totalLetters == 0) return Double.MAX_VALUE;
        double chiSquare = 0.0;
        for (int i = 0; i < 26; i++) {
            double expected = (expectedPercentages[i] / 100.0) * totalLetters;
            if (expected > 0) {
                double diff = observedCounts[i] - expected;
                chiSquare += (diff * diff) / expected;
            }
        }
        return chiSquare;
    }
}
