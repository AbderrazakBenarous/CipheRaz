package com.cryptolab.core.security;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Centralized provider for cryptographically secure pseudo-random number generators.
 * Guarantees use of NativePRNG or SHA1PRNG per security best practices.
 *
 * Developed by Abderrazak BENAROUS
 */
public final class SecureRandomProvider {

    private static final SecureRandom SECURE_RANDOM;

    static {
        SecureRandom sr;
        try {
            sr = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            sr = new SecureRandom();
        }
        SECURE_RANDOM = sr;
    }

    private SecureRandomProvider() {}

    public static SecureRandom getSecureRandom() {
        return SECURE_RANDOM;
    }
}
