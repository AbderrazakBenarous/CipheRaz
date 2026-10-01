package com.cryptolab.core.signatures;

import com.cryptolab.core.model.AlgorithmCategory;
import com.cryptolab.core.model.CryptoAlgorithm;
import com.cryptolab.core.model.CryptoResult;
import com.cryptolab.core.model.SecurityStatus;
import com.cryptolab.core.security.SecureRandomProvider;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.List;

/**
 * Educational Digital Signature Engine.
 * Supports RSA (SHA256withRSA) and ECDSA (SHA256withECDSA).
 * Demonstrates signing, verification, and tamper detection.
 *
 * Developed by Abderrazak BENAROUS
 */
public class DigitalSignatureEngine implements CryptoAlgorithm {

    public enum SignatureScheme {
        RSA_SHA256("SHA256withRSA", "RSA", 2048),
        ECDSA_P256("SHA256withECDSA", "EC", 256);

        private final String signatureAlgorithm;
        private final String keyAlgorithm;
        private final int defaultKeySize;

        SignatureScheme(String signatureAlgorithm, String keyAlgorithm, int defaultKeySize) {
            this.signatureAlgorithm = signatureAlgorithm;
            this.keyAlgorithm = keyAlgorithm;
            this.defaultKeySize = defaultKeySize;
        }

        public String getSignatureAlgorithm() {
            return signatureAlgorithm;
        }

        public String getKeyAlgorithm() {
            return keyAlgorithm;
        }

        public int getDefaultKeySize() {
            return defaultKeySize;
        }
    }

    @Override
    public String getId() {
        return "signatures";
    }

    @Override
    public String getName() {
        return "Digital Signatures & Tamper Verification";
    }

    @Override
    public String getDescription() {
        return "Asymmetric authentication primitive ensuring integrity, source authenticity, and non-repudiation.";
    }

    @Override
    public AlgorithmCategory getCategory() {
        return AlgorithmCategory.SIGNATURES;
    }

    @Override
    public SecurityStatus getSecurityStatus() {
        return SecurityStatus.SECURE_RECOMMENDED;
    }

    @Override
    public String getEducationalSummary() {
        return "Digital signatures combine hash functions with asymmetric cryptography. The sender encrypts the hash with their private key. " +
                "Anyone with the public key can verify the signature. Any tampering with either the message or the signature invalidates the verification mathematically.";
    }

    public static KeyPair generateKeyPair(SignatureScheme scheme) throws GeneralSecurityException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(scheme.getKeyAlgorithm());
        generator.initialize(scheme.getDefaultKeySize(), SecureRandomProvider.getSecureRandom());
        return generator.generateKeyPair();
    }

    public record SignatureResult(
            byte[] signatureBytes,
            String base64Signature,
            String schemeName,
            List<String> traces
    ) {}

    public static SignatureResult sign(String message, PrivateKey privateKey, SignatureScheme scheme) throws GeneralSecurityException {
        Signature signature = Signature.getInstance(scheme.getSignatureAlgorithm());
        signature.initSign(privateKey);
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        signature.update(messageBytes);

        byte[] sigBytes = signature.sign();
        String base64 = Base64.getEncoder().encodeToString(sigBytes);

        List<String> traces = List.of(
                "Step 1: Compute SHA-256 hash digest of message (" + messageBytes.length + " bytes)",
                "Step 2: Sign digest using sender's " + scheme.getKeyAlgorithm() + " Private Key",
                "Step 3: Signature generated (" + sigBytes.length + " bytes / " + (sigBytes.length * 8) + " bits)"
        );

        return new SignatureResult(sigBytes, base64, scheme.name(), traces);
    }

    public record VerificationResult(
            boolean valid,
            String statusMessage,
            List<String> traces
    ) {}

    public static VerificationResult verify(String message, byte[] signatureBytes, PublicKey publicKey, SignatureScheme scheme) {
        try {
            Signature signature = Signature.getInstance(scheme.getSignatureAlgorithm());
            signature.initVerify(publicKey);
            byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
            signature.update(messageBytes);

            boolean valid = signature.verify(signatureBytes);

            List<String> traces = List.of(
                    "Step 1: Recompute SHA-256 hash digest of the received message",
                    "Step 2: Decrypt signature using sender's Public Key",
                    "Step 3: Compare recovered digest with recomputed digest: " + (valid ? "MATCH (VALID)" : "MISMATCH (TAMPERED)")
            );

            String statusMsg = valid
                    ? "SIGNATURE VALID: The message is authentic and unaltered."
                    : "VERIFICATION FAILED: Message has been tampered with or signature is forged!";

            return new VerificationResult(valid, statusMsg, traces);
        } catch (Exception e) {
            return new VerificationResult(false, "Verification error: " + e.getMessage(), List.of("Verification exception: " + e.getMessage()));
        }
    }
}
