package com.cryptolab.core.storage;

import java.time.LocalDateTime;

/**
 * Record holding experiment execution metadata for local session history.
 * Explicitly avoids storing confidential keys, passwords, or plaintexts.
 *
 * Developed by Abderrazak BENAROUS
 */
public record ExperimentLog(
        long id,
        String createdAt,
        String algorithmName,
        String operationType,
        String parametersSummary,
        int inputSizeBytes,
        int outputSizeBytes,
        double durationMillis,
        boolean success,
        String notes
) {}
