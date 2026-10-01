package com.cryptolab.core.classical;

import com.cryptolab.core.model.AlgorithmCategory;
import com.cryptolab.core.model.CryptoAlgorithm;
import com.cryptolab.core.model.CryptoResult;
import com.cryptolab.core.model.SecurityStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Educational implementation of the Hebrew Atbash reflection cipher.
 * An involutory monoalphabetic cipher mapping the 1st letter to the 26th, 2nd to the 25th, etc.
 *
 * Developed by Abderrazak BENAROUS
 */
public class AtbashCipher implements CryptoAlgorithm {

    @Override
    public String getId() {
        return "atbash";
    }

    @Override
    public String getName() {
        return "Atbash Cipher";
    }

    @Override
    public String getDescription() {
        return "Ancient monoalphabetic substitution cipher that reverses the alphabet: A<->Z, B<->Y, C<->X.";
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
        return "Atbash is an involution: applying the transformation twice returns the original plaintext. " +
                "It has no secret key (keyspace = 1) and provides zero confidentiality.";
    }

    public CryptoResult process(String input) {
        long startTime = System.nanoTime();
        if (input == null || input.isEmpty()) {
            return CryptoResult.ok("", "Empty input provided", List.of(), 0);
        }

        StringBuilder output = new StringBuilder();
        List<String> traces = new ArrayList<>();

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (Character.isLetter(ch)) {
                char base = Character.isUpperCase(ch) ? 'A' : 'a';
                int originalPos = ch - base;
                int mirroredPos = 25 - originalPos;
                char mirroredChar = (char) (base + mirroredPos);
                output.append(mirroredChar);

                if (traces.size() < 25) {
                    traces.add(String.format("'%c' (pos %d) reflected -> '%c' (pos %d)",
                            ch, originalPos, mirroredChar, mirroredPos));
                }
            } else {
                output.append(ch);
            }
        }

        long duration = System.nanoTime() - startTime;
        String explanation = "Atbash symmetric reflection completed. Total characters processed: " + input.length();
        return CryptoResult.ok(output.toString(), explanation, traces, duration);
    }
}
