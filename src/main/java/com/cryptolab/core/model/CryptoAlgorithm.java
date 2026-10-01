package com.cryptolab.core.model;

/**
 * Common contract for cryptographic algorithms implemented in CryptoLab.
 * Enables Strategy pattern and polymorphic laboratory testing.
 *
 * Developed by Abderrazak BENAROUS
 */
public interface CryptoAlgorithm {

    String getId();

    String getName();

    String getDescription();

    AlgorithmCategory getCategory();

    SecurityStatus getSecurityStatus();

    String getEducationalSummary();
}
