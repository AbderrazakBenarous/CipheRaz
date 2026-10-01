package com.cryptolab.core.classical;

import com.cryptolab.core.model.AlgorithmCategory;
import com.cryptolab.core.model.CryptoAlgorithm;
import com.cryptolab.core.model.CryptoResult;
import com.cryptolab.core.model.SecurityStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Educational implementation of the Caesar Shift Cipher.
 * Demonstrates modular addition: C = (P + k) mod 26
 *
 * Developed by Abderrazak BENAROUS
 */
public class CaesarCipher implements CryptoAlgorithm {

    public static final int ALPHABET_SIZE = 26;

    @Override
    public String getId() {
        return "caesar";
    }

    @Override
    public String getName() {
        return "Caesar Cipher";
    }

    @Override
    public String getDescription() {
        return "Historical monoalphabetic substitution cipher shifting alphabet characters by a fixed numerical key k.";
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
        return "Caesar shift replaces each letter with another letter a fixed number of positions down the alphabet. " +
                "With only 25 possible non-trivial shifts, it provides zero modern security and is trivially broken via brute force or frequency analysis.";
    }

    public CryptoResult encrypt(String plaintext, int shift) {
        return process(plaintext, shift, true);
    }

    public CryptoResult decrypt(String ciphertext, int shift) {
        return process(ciphertext, shift, false);
    }

    private CryptoResult process(String input, int rawShift, boolean encrypt) {
        long startTime = System.nanoTime();
        if (input == null || input.isEmpty()) {
            return CryptoResult.ok("", "Empty input provided", List.of(), 0);
        }

        // Normalize shift to [0, 25]
        int effectiveShift = ((rawShift % ALPHABET_SIZE) + ALPHABET_SIZE) % ALPHABET_SIZE;
        int shift = encrypt ? effectiveShift : (ALPHABET_SIZE - effectiveShift) % ALPHABET_SIZE;

        StringBuilder output = new StringBuilder();
        List<String> traces = new ArrayList<>();

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (Character.isLetter(ch)) {
                char base = Character.isUpperCase(ch) ? 'A' : 'a';
                int originalPos = ch - base;
                int shiftedPos = (originalPos + shift) % ALPHABET_SIZE;
                char shiftedChar = (char) (base + shiftedPos);
                output.append(shiftedChar);

                if (traces.size() < 25) { // Record first 25 character steps for educational clarity
                    traces.add(String.format("'%c' (pos %d) %s %d mod 26 -> '%c' (pos %d)",
                            ch, originalPos, encrypt ? "+" : "-", effectiveShift, shiftedChar, shiftedPos));
                }
            } else {
                output.append(ch);
            }
        }

        long duration = System.nanoTime() - startTime;
        String explanation = String.format("Caesar %s completed with shift k = %d (mod 26). Processed %d characters.",
                encrypt ? "encryption" : "decryption", effectiveShift, input.length());

        return CryptoResult.ok(output.toString(), explanation, traces, duration);
    }
}
