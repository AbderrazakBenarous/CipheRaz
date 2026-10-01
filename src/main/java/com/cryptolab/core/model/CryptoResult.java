package com.cryptolab.core.model;

import java.util.Collections;
import java.util.List;

/**
 * Standardized result envelope holding cryptographic outputs, intermediate step traces,
 * educational notes, and timing metrics.
 *
 * Developed by Abderrazak BENAROUS
 */
public record CryptoResult(
        boolean success,
        String outputText,
        byte[] outputBytes,
        String educationalExplanation,
        List<String> stepTraces,
        long executionDurationNanos,
        String errorMessage
) {
    public static CryptoResult ok(String outputText, String explanation, List<String> steps, long durationNanos) {
        return new CryptoResult(
                true,
                outputText,
                outputText != null ? outputText.getBytes(java.nio.charset.StandardCharsets.UTF_8) : new byte[0],
                explanation,
                steps != null ? steps : Collections.emptyList(),
                durationNanos,
                null
        );
    }

    public static CryptoResult ok(byte[] outputBytes, String explanation, List<String> steps, long durationNanos) {
        return new CryptoResult(
                true,
                null,
                outputBytes,
                explanation,
                steps != null ? steps : Collections.emptyList(),
                durationNanos,
                null
        );
    }

    public static CryptoResult failure(String errorMessage) {
        return new CryptoResult(
                false,
                null,
                new byte[0],
                null,
                Collections.emptyList(),
                0,
                errorMessage
        );
    }
}
