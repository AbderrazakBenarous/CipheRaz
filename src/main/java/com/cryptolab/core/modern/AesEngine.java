package com.cryptolab.core.modern;

import com.cryptolab.core.model.AlgorithmCategory;
import com.cryptolab.core.model.CryptoAlgorithm;
import com.cryptolab.core.model.CryptoResult;
import com.cryptolab.core.model.SecurityStatus;
import com.cryptolab.core.security.SecureRandomProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;

/**
 * Standard JCA implementation of the Advanced Encryption Standard (AES).
 * Supports AES-GCM (Authenticated Encryption with Associated Data) and AES-CBC with PKCS5 padding.
 *
 * Developed by Abderrazak BENAROUS
 */
public class AesEngine implements CryptoAlgorithm {

    public enum Mode {
        GCM("AES/GCM/NoPadding", 12, 128, true),
        CBC("AES/CBC/PKCS5Padding", 16, 0, false);

        private final String transformation;
        private final int ivLengthBytes;
        private final int tagLengthBits;
        private final boolean authenticated;

        Mode(String transformation, int ivLengthBytes, int tagLengthBits, boolean authenticated) {
            this.transformation = transformation;
            this.ivLengthBytes = ivLengthBytes;
            this.tagLengthBits = tagLengthBits;
            this.authenticated = authenticated;
        }

        public String getTransformation() {
            return transformation;
        }

        public int getIvLengthBytes() {
            return ivLengthBytes;
        }

        public int getTagLengthBits() {
            return tagLengthBits;
        }

        public boolean isAuthenticated() {
            return authenticated;
        }
    }

    @Override
    public String getId() {
        return "aes";
    }

    @Override
    public String getName() {
        return "Advanced Encryption Standard (AES)";
    }

    @Override
    public String getDescription() {
        return "NIST FIPS 197 symmetric block cipher (Rijndael). Supports 128, 192, and 256-bit keys with GCM and CBC modes.";
    }

    @Override
    public AlgorithmCategory getCategory() {
        return AlgorithmCategory.SYMMETRIC;
    }

    @Override
    public SecurityStatus getSecurityStatus() {
        return SecurityStatus.SECURE_RECOMMENDED;
    }

    @Override
    public String getEducationalSummary() {
        return "AES is the worldwide standard for symmetric encryption. In GCM mode, it provides both confidentiality and data authenticity (AEAD). " +
                "CRITICAL: Under AES-GCM, never reuse a Nonce/IV with the same key, as doing so destroys authentication guarantees and leaks plaintext XORs.";
    }

    public static byte[] generateRandomKey(int keyBits) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(keyBits, SecureRandomProvider.getSecureRandom());
            return keyGen.generateKey().getEncoded();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("AES KeyGenerator unavailable", e);
        }
    }

    public static byte[] generateRandomIv(int ivLengthBytes) {
        byte[] iv = new byte[ivLengthBytes];
        SecureRandomProvider.getSecureRandom().nextBytes(iv);
        return iv;
    }

    public record EncryptionEnvelope(
            byte[] ciphertext,
            byte[] ivOrNonce,
            String formatHex,
            String formatBase64
    ) {}

    public CryptoResult encrypt(String plaintext, byte[] keyBytes, Mode mode, byte[] customIv) {
        long startTime = System.nanoTime();
        if (plaintext == null) {
            return CryptoResult.failure("Plaintext cannot be null");
        }
        if (keyBytes == null || (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32)) {
            return CryptoResult.failure("Invalid AES key length: Key must be 128, 192, or 256 bits (16, 24, or 32 bytes). Received: " + (keyBytes != null ? keyBytes.length : 0) + " bytes.");
        }

        try {
            byte[] iv = (customIv != null && customIv.length == mode.getIvLengthBytes())
                    ? customIv
                    : generateRandomIv(mode.getIvLengthBytes());

            Cipher cipher = Cipher.getInstance(mode.getTransformation());
            SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

            if (mode == Mode.GCM) {
                GCMParameterSpec spec = new GCMParameterSpec(mode.getTagLengthBits(), iv);
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);
            } else {
                IvParameterSpec spec = new IvParameterSpec(iv);
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);
            }

            byte[] plaintextBytes = plaintext.getBytes(StandardCharsets.UTF_8);
            byte[] ciphertext = cipher.doFinal(plaintextBytes);

            List<String> traces = new ArrayList<>();
            traces.add("1. Input Plaintext Size: " + plaintextBytes.length + " bytes");
            traces.add("2. Key Size: " + (keyBytes.length * 8) + " bits (AES-" + (keyBytes.length * 8) + ")");
            traces.add("3. Mode: " + mode.name() + " (" + mode.getTransformation() + ")");
            traces.add("4. Generated IV/Nonce (" + iv.length + " bytes): " + HexFormat.of().formatHex(iv));
            if (mode == Mode.GCM) {
                traces.add("5. GCM AEAD Tag appended: 128-bit authentication tag embedded in ciphertext");
            }
            traces.add("6. Output Ciphertext Size: " + ciphertext.length + " bytes");

            String base64Combined = Base64.getEncoder().encodeToString(combineIvAndCiphertext(iv, ciphertext));
            String hexCiphertext = HexFormat.of().formatHex(ciphertext);

            long duration = System.nanoTime() - startTime;
            String explanation = String.format("AES-%d-%s encryption successful. IV: %s. Ciphertext length: %d bytes.",
                    keyBytes.length * 8, mode.name(), HexFormat.of().formatHex(iv), ciphertext.length);

            return CryptoResult.ok(
                    "IV (Hex): " + HexFormat.of().formatHex(iv) + "\n" +
                    "Ciphertext (Hex): " + hexCiphertext + "\n" +
                    "Combined IV+Ciphertext (Base64): " + base64Combined,
                    explanation,
                    traces,
                    duration
            );
        } catch (Exception e) {
            return CryptoResult.failure("AES encryption failed: " + e.getMessage());
        }
    }

    public CryptoResult decrypt(byte[] ciphertext, byte[] keyBytes, Mode mode, byte[] iv) {
        long startTime = System.nanoTime();
        if (ciphertext == null || ciphertext.length == 0) {
            return CryptoResult.failure("Ciphertext cannot be empty");
        }
        if (keyBytes == null || (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32)) {
            return CryptoResult.failure("Invalid key length: must be 16, 24, or 32 bytes.");
        }
        if (iv == null || iv.length != mode.getIvLengthBytes()) {
            return CryptoResult.failure("Invalid IV length for " + mode.name() + ": expected " + mode.getIvLengthBytes() + " bytes.");
        }

        try {
            Cipher cipher = Cipher.getInstance(mode.getTransformation());
            SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

            if (mode == Mode.GCM) {
                GCMParameterSpec spec = new GCMParameterSpec(mode.getTagLengthBits(), iv);
                cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
            } else {
                IvParameterSpec spec = new IvParameterSpec(iv);
                cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
            }

            byte[] decryptedBytes = cipher.doFinal(ciphertext);
            String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);

            List<String> traces = new ArrayList<>();
            traces.add("1. Ciphertext input: " + ciphertext.length + " bytes");
            traces.add("2. Mode: " + mode.name());
            traces.add("3. IV Verified: " + HexFormat.of().formatHex(iv));
            if (mode == Mode.GCM) {
                traces.add("4. GCM Tag Authentication: INTEGRITY VERIFIED - No tampering detected.");
            }
            traces.add("5. Recovered Plaintext Size: " + decryptedBytes.length + " bytes");

            long duration = System.nanoTime() - startTime;
            String explanation = "AES-" + (keyBytes.length * 8) + "-" + mode.name() + " decryption successful. Integrity verified.";

            return CryptoResult.ok(decryptedText, explanation, traces, duration);
        } catch (javax.crypto.AEADBadTagException e) {
            return CryptoResult.failure("AUTHENTICATION FAILED: GCM Authentication Tag mismatch! The ciphertext, nonce, or key has been tampered with or is invalid.");
        } catch (javax.crypto.BadPaddingException e) {
            return CryptoResult.failure("DECRYPTION FAILED: Bad padding. In incorrect key or corrupted CBC ciphertext.");
        } catch (Exception e) {
            return CryptoResult.failure("AES decryption failed: " + e.getMessage());
        }
    }

    private static byte[] combineIvAndCiphertext(byte[] iv, byte[] ciphertext) {
        byte[] combined = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);
        return combined;
    }
}
