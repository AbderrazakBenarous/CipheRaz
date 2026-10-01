package com.cryptolab.core.classical;

import com.cryptolab.core.model.AlgorithmCategory;
import com.cryptolab.core.model.CryptoAlgorithm;
import com.cryptolab.core.model.CryptoResult;
import com.cryptolab.core.model.SecurityStatus;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Educational implementation of the Playfair Digraph Substitution Cipher.
 * Encrypts pairs of letters using a 5x5 key matrix (I/J combined).
 *
 * Developed by Abderrazak BENAROUS
 */
public class PlayfairCipher implements CryptoAlgorithm {

    @Override
    public String getId() {
        return "playfair";
    }

    @Override
    public String getName() {
        return "Playfair Cipher";
    }

    @Override
    public String getDescription() {
        return "First practical digraph substitution cipher using a 5x5 key matrix, breaking single-letter frequency analysis.";
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
        return "Playfair encrypts pairs of letters (digraphs) instead of single letters, increasing the search space to 25x25 = 600 digraphs. " +
                "However, digraph frequency analysis still breaks Playfair easily with sufficient ciphertext.";
    }

    public char[][] generateKeyMatrix(String keyword) {
        String cleanKey = (keyword != null ? keyword : "").toUpperCase().replaceAll("[^A-Z]", "").replace("J", "I");
        Set<Character> used = new LinkedHashSet<>();

        for (char c : cleanKey.toCharArray()) {
            used.add(c);
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            if (c != 'J') {
                used.add(c);
            }
        }

        char[][] matrix = new char[5][5];
        int idx = 0;
        for (char c : used) {
            matrix[idx / 5][idx % 5] = c;
            idx++;
        }
        return matrix;
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

        char[][] matrix = generateKeyMatrix(keyword);
        String cleaned = input.toUpperCase().replaceAll("[^A-Z]", "").replace("J", "I");

        if (cleaned.isEmpty()) {
            return CryptoResult.failure("Input contains no alphabetic characters.");
        }

        // Prepare digraphs
        List<String> digraphs = new ArrayList<>();
        if (encrypt) {
            int i = 0;
            while (i < cleaned.length()) {
                char first = cleaned.charAt(i);
                char second;
                if (i + 1 < cleaned.length()) {
                    second = cleaned.charAt(i + 1);
                    if (first == second) {
                        second = 'X';
                        i++;
                    } else {
                        i += 2;
                    }
                } else {
                    second = 'X';
                    i++;
                }
                digraphs.add("" + first + second);
            }
        } else {
            for (int i = 0; i < cleaned.length(); i += 2) {
                if (i + 1 < cleaned.length()) {
                    digraphs.add(cleaned.substring(i, i + 2));
                } else {
                    digraphs.add(cleaned.substring(i) + "X");
                }
            }
        }

        StringBuilder output = new StringBuilder();
        List<String> traces = new ArrayList<>();

        for (String pair : digraphs) {
            char c1 = pair.charAt(0);
            char c2 = pair.charAt(1);

            int[] p1 = findPosition(matrix, c1);
            int[] p2 = findPosition(matrix, c2);

            char o1, o2;
            String ruleUsed;

            if (p1[0] == p2[0]) {
                // Same row
                int shift = encrypt ? 1 : 4;
                o1 = matrix[p1[0]][(p1[1] + shift) % 5];
                o2 = matrix[p2[0]][(p2[1] + shift) % 5];
                ruleUsed = "Same Row (shift horizontally)";
            } else if (p1[1] == p2[1]) {
                // Same column
                int shift = encrypt ? 1 : 4;
                o1 = matrix[(p1[0] + shift) % 5][p1[1]];
                o2 = matrix[(p2[0] + shift) % 5][p2[1]];
                ruleUsed = "Same Column (shift vertically)";
            } else {
                // Rectangle
                o1 = matrix[p1[0]][p2[1]];
                o2 = matrix[p2[0]][p1[1]];
                ruleUsed = "Rectangle (swap opposite corners)";
            }

            output.append(o1).append(o2);
            if (traces.size() < 25) {
                traces.add(String.format("Digraph [%c%c] -> [%c%c] via %s", c1, c2, o1, o2, ruleUsed));
            }
        }

        long duration = System.nanoTime() - startTime;
        String explanation = String.format("Playfair 5x5 matrix %s completed on %d digraph pairs.",
                encrypt ? "encryption" : "decryption", digraphs.size());

        return CryptoResult.ok(output.toString(), explanation, traces, duration);
    }

    private int[] findPosition(char[][] matrix, char c) {
        for (int r = 0; r < 5; r++) {
            for (int col = 0; col < 5; col++) {
                if (matrix[r][col] == c) {
                    return new int[]{r, col};
                }
            }
        }
        return new int[]{0, 0};
    }
}
