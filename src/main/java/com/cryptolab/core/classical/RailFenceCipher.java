package com.cryptolab.core.classical;

import com.cryptolab.core.model.AlgorithmCategory;
import com.cryptolab.core.model.CryptoAlgorithm;
import com.cryptolab.core.model.CryptoResult;
import com.cryptolab.core.model.SecurityStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Educational implementation of the Rail Fence Transposition Cipher.
 * Writes letters in a zigzag pattern across N rails and reads row-by-row.
 *
 * Developed by Abderrazak BENAROUS
 */
public class RailFenceCipher implements CryptoAlgorithm {

    @Override
    public String getId() {
        return "railfence";
    }

    @Override
    public String getName() {
        return "Rail Fence Cipher";
    }

    @Override
    public String getDescription() {
        return "Transposition cipher that writes characters in a diagonal zigzag pattern along N horizontal rails.";
    }

    @Override
    public AlgorithmCategory getCategory() {
        return AlgorithmCategory.CLASSICAL;
    }

    @Override
    public SecurityStatus getSecurityStatus() {
        return SecurityStatus.CLASSICAL_BROKEN;
    }

    @Override
    public String getEducationalSummary() {
        return "Rail Fence preserves character frequencies completely (the histogram remains identical to plaintext). " +
                "Only the position of letters is permuted. The keyspace is limited to the message length (typically 2 to 10 rails).";
    }

    public CryptoResult encrypt(String plaintext, int rails) {
        long startTime = System.nanoTime();
        if (plaintext == null || plaintext.isEmpty()) {
            return CryptoResult.ok("", "Empty input provided", List.of(), 0);
        }
        if (rails <= 1 || rails >= plaintext.length()) {
            return CryptoResult.ok(plaintext, "Trivial rail count (rails <= 1 or >= length); output matches input.", List.of(), 0);
        }

        StringBuilder[] railBuilders = new StringBuilder[rails];
        for (int r = 0; r < rails; r++) railBuilders[r] = new StringBuilder();

        int currentRail = 0;
        boolean goingDown = true;
        List<String> traces = new ArrayList<>();

        for (int i = 0; i < plaintext.length(); i++) {
            char ch = plaintext.charAt(i);
            railBuilders[currentRail].append(ch);

            if (traces.size() < 25) {
                traces.add(String.format("Char '%c' at index %d -> placed on Rail %d", ch, i, currentRail + 1));
            }

            if (currentRail == 0) {
                goingDown = true;
            } else if (currentRail == rails - 1) {
                goingDown = false;
            }
            currentRail += goingDown ? 1 : -1;
        }

        StringBuilder output = new StringBuilder();
        for (StringBuilder rb : railBuilders) {
            output.append(rb);
        }

        long duration = System.nanoTime() - startTime;
        String explanation = String.format("Rail Fence transposition across %d rails. Letter frequencies remain unchanged.", rails);
        return CryptoResult.ok(output.toString(), explanation, traces, duration);
    }

    public CryptoResult decrypt(String ciphertext, int rails) {
        long startTime = System.nanoTime();
        if (ciphertext == null || ciphertext.isEmpty()) {
            return CryptoResult.ok("", "Empty input provided", List.of(), 0);
        }
        if (rails <= 1 || rails >= ciphertext.length()) {
            return CryptoResult.ok(ciphertext, "Trivial rail count; output matches input.", List.of(), 0);
        }

        int n = ciphertext.length();
        // Mark zigzag positions
        char[][] grid = new char[rails][n];
        for (char[] row : grid) Arrays.fill(row, '\n');

        int currentRail = 0;
        boolean goingDown = true;
        for (int col = 0; col < n; col++) {
            grid[currentRail][col] = '*';
            if (currentRail == 0) goingDown = true;
            else if (currentRail == rails - 1) goingDown = false;
            currentRail += goingDown ? 1 : -1;
        }

        // Fill characters row by row into marked positions
        int charIdx = 0;
        for (int r = 0; r < rails; r++) {
            for (int col = 0; col < n; col++) {
                if (grid[r][col] == '*' && charIdx < n) {
                    grid[r][col] = ciphertext.charAt(charIdx++);
                }
            }
        }

        // Read off in zigzag order
        StringBuilder output = new StringBuilder();
        currentRail = 0;
        goingDown = true;
        List<String> traces = new ArrayList<>();

        for (int col = 0; col < n; col++) {
            char ch = grid[currentRail][col];
            output.append(ch);
            if (traces.size() < 25) {
                traces.add(String.format("Zigzag step at index %d: Rail %d yielded '%c'", col, currentRail + 1, ch));
            }
            if (currentRail == 0) goingDown = true;
            else if (currentRail == rails - 1) goingDown = false;
            currentRail += goingDown ? 1 : -1;
        }

        long duration = System.nanoTime() - startTime;
        String explanation = String.format("Rail Fence reconstruction across %d rails completed.", rails);
        return CryptoResult.ok(output.toString(), explanation, traces, duration);
    }
}
