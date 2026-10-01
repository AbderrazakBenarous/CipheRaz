package com.cryptolab.core.security;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.KeySpec;

/**
 * Secure password-based key derivation function (PBKDF2 with HMAC-SHA256).
 * Follows NIST SP 800-132 recommendations with salt and 65,536 iterations.
 *
 * Developed by Abderrazak BENAROUS
 */
public final class PasswordKdf {

    public static final int ITERATIONS = 65536;
    public static final int KEY_LENGTH_BITS = 256;

    private PasswordKdf() {}

    public static SecretKey deriveAesGcmKey(char[] password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH_BITS);
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }
}
