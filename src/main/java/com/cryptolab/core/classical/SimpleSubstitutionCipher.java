package com.cryptolab.core.classical;

import com.cryptolab.core.model.AlgorithmCategory;
import com.cryptolab.core.model.CryptoAlgorithm;
import com.cryptolab.core.model.CryptoResult;
import com.cryptolab.core.model.SecurityStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Educational implementation of the Monoalphabetic Simple Substitution Cipher.
 * Uses an arbitrary 26-letter permutation alphabet.
 *
 * Developed by Abderrazak BENAROUS
 */
public class SimpleSubstitutionCipher implements CryptoAlgorithm {

    public static final String STANDARD_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Override
    public String getId() {
        return "substitution";
    }

    @Override
    public String getName() {
        return "Simple Substitution Cipher";
    }

    @Override
    public String getDescription() {
        return "Monoalphabetic cipher replacing each letter of the alphabet with a mapped letter from a 26-character permutation key.";
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
        return "Although the keyspace is 26! ≈ 4.03 x 10^26 (approx 88 bits of security, immune to brute force in Caesar's era), " +
                "monoalphabetic substitution completely preserves single-letter and digram frequencies. " +
                "It can be deciphered using frequency analysis, word patterns, and vowel identification.";
    }

    public static boolean isValidKey(String key) {
        if (key == null || key.length() != 26) return false;
        Set<Character> chars = new HashSet<>();
        for (char c : key.toUpperCase().toCharArray()) {
            if (c < 'A' || c > 'Z' || !chars.add(c)) {
                return false;
            }
        }
        return chars.size() == 26;
    }

    public CryptoResult encrypt(String plaintext, String cipherAlphabet) {
        return process(plaintext, cipherAlphabet, true);
    }

    public CryptoResult decrypt(String ciphertext, String cipherAlphabet) {
        return process(ciphertext, cipherAlphabet, false);
    }

    private CryptoResult process(String input, String cipherAlphabet, boolean encrypt) {
        long startTime = System.nanoTime();
        if (input == null || input.isEmpty()) {
            return CryptoResult.ok("", "Empty input provided", List.of(), 0);
        }

        if (cipherAlphabet == null || !isValidKey(cipherAlphabet)) {
            return CryptoResult.failure("Substitution key must contain exactly 26 unique English alphabet letters (A-Z).");
        }

        String key = cipherAlphabet.toUpperCase();
        StringBuilder output = new StringBuilder();
        List<String> traces = new ArrayList<>();

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (Character.isLetter(ch)) {
                boolean isUpper = Character.isUpperCase(ch);
                char upperCh = Character.toUpperCase(ch);
                char mapped;

                if (encrypt) {
                    int idx = upperCh - 'A';
                    mapped = key.charAt(idx);
                    if (traces.size() < 25) {
                        traces.add(String.format("Plain '%c' -> Cipher '%c'", upperCh, mapped));
                    }
                } else {
                    int idx = key.indexOf(upperCh);
                    mapped = (char) ('A' + idx);
                    if (traces.size() < 25) {
                        traces.add(String.format("Cipher '%c' -> Plain '%c'", upperCh, mapped));
                    }
                }

                output.append(isUpper ? mapped : Character.toLowerCase(mapped));
            } else {
                output.append(ch);
            }
        }

        long duration = System.nanoTime() - startTime;
        String explanation = String.format("Simple substitution %s completed with 26-letter custom alphabet.",
                encrypt ? "encryption" : "decryption");

        return CryptoResult.ok(output.toString(), explanation, traces, duration);
    }
}
