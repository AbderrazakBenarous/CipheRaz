package com.cryptolab.core.cryptanalysis;

import com.cryptolab.core.classical.CaesarCipher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests validating cryptanalytic frequency metrics, Index of Coincidence (IoC),
 * and automated Caesar brute-force recovery.
 *
 * Developed by Abderrazak BENAROUS
 */
class CryptanalysisTest {

    @Test
    @DisplayName("Frequency analysis: letter counting, percentages, and Index of Coincidence")
    void testFrequencyAnalysis() {
        String sampleText = "THIS IS A COMPREHENSIVE CYBERSECURITY RESEARCH LABORATORY FOR UNIVERSITY STUDENTS.";
        FrequencyAnalysis.AnalysisResult result = FrequencyAnalysis.analyze(sampleText);

        assertThat(result.totalLetters()).isGreaterThan(50);
        assertThat(result.letterCounts()).hasSize(26);
        assertThat(result.letterPercentages()).hasSize(26);

        // English text typically has an IoC around 0.055 to 0.075
        assertThat(result.indexOfCoincidence()).isBetween(0.045, 0.085);
        assertThat(result.chiSquareEnglish()).isGreaterThan(0.0);
    }

    @Test
    @DisplayName("Caesar brute-force attack: successfully recovers correct shift from ciphertext")
    void testCaesarAttack() {
        CaesarCipher cipher = new CaesarCipher();
        CaesarAttack attack = new CaesarAttack();

        String plaintext = "THE ADVANCED ENCRYPTION STANDARD AND COMPUTER FORENSICS CURRICULUM ARE ESSENTIAL FOR DEFENSE.";
        int expectedShift = 9;

        String ciphertext = cipher.encrypt(plaintext, expectedShift).outputText();

        List<CaesarAttack.AttackCandidate> candidates = attack.executeAttack(ciphertext);
        assertThat(candidates).hasSize(25);

        // Best candidate should have the lowest Chi-Square and match expected shift
        CaesarAttack.AttackCandidate best = candidates.get(0);
        assertThat(best.shiftUsed()).isEqualTo(expectedShift);
        assertThat(best.candidateText()).isEqualTo(plaintext);
        assertThat(best.confidence()).contains("High");
    }
}
