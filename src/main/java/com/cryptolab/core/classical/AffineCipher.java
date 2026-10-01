package com.cryptolab.core.classical;

import com.cryptolab.core.model.AlgorithmCategory;
import com.cryptolab.core.model.CryptoAlgorithm;
import com.cryptolab.core.model.CryptoResult;
import com.cryptolab.core.model.SecurityStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Educational implementation of the Affine Cipher.
 * Encrypt: C = (a * P + b) mod 26
 * Decrypt: P = a^(-1) * (C - b) mod 26
 * Requirement: gcd(a, 26) == 1
 *
 * Developed by Abderrazak BENAROUS
 */
public class AffineCipher implements CryptoAlgorithm {

    public static final int ALPHABET_SIZE = 26;
    public static final List<Integer> VALID_A_VALUES = List.of(1, 3, 5, 7, 9, 11, 15, 17, 19, 21, 23, 25);

    @Override
    public String getId() {
        return "affine";
    }

    @Override
    public String getName() {
        return "Affine Cipher";
    }

    @Override
    public String getDescription() {
        return "Monoalphabetic cipher combining multiplicative and additive modular arithmetic: C = (a*P + b) mod 26.";
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
        return "The Affine cipher requires 'a' to be coprime with 26 so that modular inversion is possible. " +
                "The total keyspace is only 12 * 26 = 312 possible keys, making exhaustive brute force instantaneous.";
    }

    public static int gcd(int a, int b) {
        return b == 0 ? Math.abs(a) : gcd(b, a % b);
    }

    public static int modInverse(int a, int m) {
        a = ((a % m) + m) % m;
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1) {
                return x;
            }
        }
        return -1;
    }

    public CryptoResult encrypt(String plaintext, int a, int b) {
        return process(plaintext, a, b, true);
    }

    public CryptoResult decrypt(String ciphertext, int a, int b) {
        return process(ciphertext, a, b, false);
    }

    private CryptoResult process(String input, int a, int b, boolean encrypt) {
        long startTime = System.nanoTime();
        if (input == null || input.isEmpty()) {
            return CryptoResult.ok("", "Empty input provided", List.of(), 0);
        }

        if (gcd(a, ALPHABET_SIZE) != 1) {
            return CryptoResult.failure(String.format(
                    "Parameter 'a' (%d) is not coprime with 26 (gcd = %d). Modular inverse does not exist. Choose from: %s",
                    a, gcd(a, ALPHABET_SIZE), VALID_A_VALUES));
        }

        int aNorm = ((a % ALPHABET_SIZE) + ALPHABET_SIZE) % ALPHABET_SIZE;
        int bNorm = ((b % ALPHABET_SIZE) + ALPHABET_SIZE) % ALPHABET_SIZE;
        int aInv = modInverse(aNorm, ALPHABET_SIZE);

        StringBuilder output = new StringBuilder();
        List<String> traces = new ArrayList<>();

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (Character.isLetter(ch)) {
                char base = Character.isUpperCase(ch) ? 'A' : 'a';
                int x = ch - base;
                int y;
                if (encrypt) {
                    y = (aNorm * x + bNorm) % ALPHABET_SIZE;
                    if (traces.size() < 25) {
                        traces.add(String.format("'%c'(%d) -> (%d*%d + %d) mod 26 = %d -> '%c'",
                                ch, x, aNorm, x, bNorm, y, (char)(base + y)));
                    }
                } else {
                    int diff = ((x - bNorm) % ALPHABET_SIZE + ALPHABET_SIZE) % ALPHABET_SIZE;
                    y = (aInv * diff) % ALPHABET_SIZE;
                    if (traces.size() < 25) {
                        traces.add(String.format("'%c'(%d) -> %d*(%d - %d) mod 26 = %d -> '%c'",
                                ch, x, aInv, x, bNorm, y, (char)(base + y)));
                    }
                }
                output.append((char) (base + y));
            } else {
                output.append(ch);
            }
        }

        long duration = System.nanoTime() - startTime;
        String explanation = String.format("Affine %s with a=%d (inv=%d), b=%d. Keyspace size: 312.",
                encrypt ? "encryption" : "decryption", aNorm, aInv, bNorm);
        return CryptoResult.ok(output.toString(), explanation, traces, duration);
    }
}
