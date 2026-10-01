package com.cryptolab.core.classical;

import com.cryptolab.core.model.AlgorithmCategory;
import com.cryptolab.core.model.CryptoAlgorithm;
import com.cryptolab.core.model.CryptoResult;
import com.cryptolab.core.model.SecurityStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Educational implementation of the Vigenère Polyalphabetic Cipher.
 * Demonstrates cyclic key addition: C_i = (P_i + K_{i mod L}) mod 26
 *
 * Developed by Abderrazak BENAROUS
 */
public class VigenereCipher implements CryptoAlgorithm {

    public static final int ALPHABET_SIZE = 26;

    @Override
    public String getId() {
        return "vigenere";
    }

    @Override
    public String getName() {
        return "Vigenère Cipher";
    }

    @Override
    public String getDescription() {
        return "Polyalphabetic substitution cipher that uses a repeating keyword to apply distinct Caesar shifts to successive characters.";
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
        return "Historically called 'le chiffre indéchiffrable', Vigenère flattens monoalphabetic frequency spikes. " +
                "However, it is vulnerable to Kasiski examination and the Index of Coincidence (Friedman test) to determine key length, followed by Caesar frequency attacks.";
    }

    public CryptoResult encrypt(String plaintext, String keyword) {
        return process(plaintext, keyword, true);
    }

    public CryptoResult decrypt(String ciphertext, String keyword) {
        return process(ciphertext, keyword, false);
    }

    private CryptoResult process(String input, String keyword, boolean encrypt) {
        long startTime = System.nanoTime();
        if (input == null || input.isEmpty()) {
            return CryptoResult.ok("", "Empty input provided", List.of(), 0);
        }

        if (keyword == null || keyword.trim().isEmpty()) {
            return CryptoResult.failure("Keyword cannot be empty. Please provide an alphabetic keyword.");
        }

        String cleanKey = keyword.replaceAll("[^a-zA-Z]", "").toUpperCase();
        if (cleanKey.isEmpty()) {
            return CryptoResult.failure("Keyword must contain at least one alphabetic letter.");
        }

        StringBuilder output = new StringBuilder();
        List<String> traces = new ArrayList<>();
        int keyIndex = 0;

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (Character.isLetter(ch)) {
                char base = Character.isUpperCase(ch) ? 'A' : 'a';
                int p = ch - base;
                char keyChar = cleanKey.charAt(keyIndex % cleanKey.length());
                int k = keyChar - 'A';

                int c;
                if (encrypt) {
                    c = (p + k) % ALPHABET_SIZE;
                    if (traces.size() < 25) {
                        traces.add(String.format("P:'%c'(%d) + K:'%c'(%d) mod 26 -> C:'%c'(%d)",
                                ch, p, keyChar, k, (char)(base + c), c));
                    }
                } else {
                    c = ((p - k) % ALPHABET_SIZE + ALPHABET_SIZE) % ALPHABET_SIZE;
                    if (traces.size() < 25) {
                        traces.add(String.format("C:'%c'(%d) - K:'%c'(%d) mod 26 -> P:'%c'(%d)",
                                ch, p, keyChar, k, (char)(base + c), c));
                    }
                }

                output.append((char) (base + c));
                keyIndex++;
            } else {
                output.append(ch);
            }
        }

        long duration = System.nanoTime() - startTime;
        String explanation = String.format("Vigenère %s with key '%s' (length %d). Key cycle repeated across %d letters.",
                encrypt ? "encryption" : "decryption", cleanKey, cleanKey.length(), keyIndex);

        return CryptoResult.ok(output.toString(), explanation, traces, duration);
    }
}
