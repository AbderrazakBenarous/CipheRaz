package com.cryptolab.core.cryptanalysis;

import com.cryptolab.core.classical.CaesarCipher;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Educational automated brute-force attack engine for the Caesar cipher.
 * Evaluates all 25 potential shifts and scores candidate plaintexts using
 * Chi-Square statistical comparison against natural language frequencies.
 *
 * Developed by Abderrazak BENAROUS
 */
public class CaesarAttack {

    private final CaesarCipher cipher = new CaesarCipher();

    public record AttackCandidate(
            int shiftUsed,
            String candidateText,
            double chiSquareScore,
            String confidence
    ) {}

    public List<AttackCandidate> executeAttack(String ciphertext) {
        if (ciphertext == null || ciphertext.trim().isEmpty()) {
            return List.of();
        }

        List<AttackCandidate> candidates = new ArrayList<>();

        for (int shift = 1; shift < 26; shift++) {
            String decrypted = cipher.decrypt(ciphertext, shift).outputText();
            FrequencyAnalysis.AnalysisResult analysis = FrequencyAnalysis.analyze(decrypted);

            String confidence;
            if (analysis.chiSquareEnglish() < 35.0) {
                confidence = "High Confidence (Strong English match)";
            } else if (analysis.chiSquareEnglish() < 80.0) {
                confidence = "Moderate Confidence";
            } else {
                confidence = "Low Probability";
            }

            candidates.add(new AttackCandidate(shift, decrypted, analysis.chiSquareEnglish(), confidence));
        }

        // Sort by chi-square ascending (lower chi-square means closer match to English)
        candidates.sort(Comparator.comparingDouble(AttackCandidate::chiSquareScore));
        return candidates;
    }
}
