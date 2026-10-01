package com.cryptolab.core.model;

/**
 * Categorization of cryptographic and steganographic domains in CryptoLab.
 *
 * Developed by Abderrazak BENAROUS
 */
public enum AlgorithmCategory {
    CLASSICAL("Classical Cryptography", "Historic substitution, transposition, and polyalphabetic ciphers"),
    SYMMETRIC("Symmetric Cryptography", "Modern block and stream ciphers (AES, ChaCha20)"),
    ASYMMETRIC("Asymmetric Cryptography", "Public-key cryptography, key exchange, and modular arithmetic (RSA, ECC, DH)"),
    HASHING("Hash Functions & MACs", "Cryptographic digests and integrity verification (MD5, SHA-1, SHA-256, SHA-512)"),
    SIGNATURES("Digital Signatures", "Asymmetric signing, verification, and tamper detection"),
    CRYPTANALYSIS("Cryptanalysis & Attacks", "Frequency analysis, brute force, and educational attack simulations"),
    STEGANOGRAPHY("Steganography & Steganalysis", "Data concealment in lossless images, LSB mapping, and detection"),
    LEARNING("Interactive Learning", "Guided concept walkthroughs, algorithm explorer, and student exercises");

    private final String displayName;
    private final String description;

    AlgorithmCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
