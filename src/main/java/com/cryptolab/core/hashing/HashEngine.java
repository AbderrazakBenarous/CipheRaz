package com.cryptolab.core.hashing;

import com.cryptolab.core.model.AlgorithmCategory;
import com.cryptolab.core.model.CryptoAlgorithm;
import com.cryptolab.core.model.SecurityStatus;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Educational Cryptographic Hashing Engine.
 * Supports MD5, SHA-1, SHA-256, SHA-384, SHA-512, and calculates the Avalanche Effect.
 *
 * Developed by Abderrazak BENAROUS
 */
public class HashEngine implements CryptoAlgorithm {

    public enum HashType {
        MD5("MD5", 128, SecurityStatus.DEPRECATED,
                "Vulnerable to practical collision attacks since 2004 (Wang et al.). Flame malware forged MD5 signatures. Strictly historical."),
        SHA1("SHA-1", 160, SecurityStatus.DEPRECATED,
                "Vulnerable to practical collision attacks (SHAttered attack, 2017). Prohibited by NIST since 2011."),
        SHA256("SHA-256", 256, SecurityStatus.SECURE_RECOMMENDED,
                "NIST FIPS 180-4 approved standard. 256-bit collision resistance. Widely used in TLS, Bitcoin, and digital signatures."),
        SHA384("SHA-384", 384, SecurityStatus.SECURE_RECOMMENDED,
                "Truncated SHA-512 variant providing 384-bit output. Immune to length-extension attacks on SHA-512."),
        SHA512("SHA-512", 512, SecurityStatus.SECURE_RECOMMENDED,
                "NIST approved high-security hash with 512-bit digest, optimized for 64-bit microprocessors.");

        private final String algorithmName;
        private final int digestBits;
        private final SecurityStatus securityStatus;
        private final String advisoryNote;

        HashType(String algorithmName, int digestBits, SecurityStatus securityStatus, String advisoryNote) {
            this.algorithmName = algorithmName;
            this.digestBits = digestBits;
            this.securityStatus = securityStatus;
            this.advisoryNote = advisoryNote;
        }

        public String getAlgorithmName() {
            return algorithmName;
        }

        public int getDigestBits() {
            return digestBits;
        }

        public SecurityStatus getSecurityStatus() {
            return securityStatus;
        }

        public String getAdvisoryNote() {
            return advisoryNote;
        }
    }

    @Override
    public String getId() {
        return "hashing";
    }

    @Override
    public String getName() {
        return "Cryptographic Hash Functions";
    }

    @Override
    public String getDescription() {
        return "One-way mathematical functions generating fixed-size digests with pre-image and collision resistance.";
    }

    @Override
    public AlgorithmCategory getCategory() {
        return AlgorithmCategory.HASHING;
    }

    @Override
    public SecurityStatus getSecurityStatus() {
        return SecurityStatus.SECURE_RECOMMENDED;
    }

    @Override
    public String getEducationalSummary() {
        return "Cryptographic hash functions must satisfy three core properties: Pre-image resistance (one-way), " +
                "Second pre-image resistance (weak collision), and Collision resistance (strong collision). " +
                "They must exhibit a strong Avalanche Effect: changing a single bit in the input flips ~50% of digest bits.";
    }

    public static byte[] hash(String input, HashType type) {
        try {
            MessageDigest md = MessageDigest.getInstance(type.getAlgorithmName());
            return md.digest(input.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Digest algorithm unavailable: " + type.getAlgorithmName(), e);
        }
    }

    public static String hashToHex(String input, HashType type) {
        return HexFormat.of().formatHex(hash(input, type));
    }

    // -------------------------------------------------------------
    // Avalanche Effect Analysis
    // -------------------------------------------------------------
    public record AvalancheAnalysis(
            String originalInput,
            String modifiedInput,
            String originalDigestHex,
            String modifiedDigestHex,
            int totalDigestBits,
            int flippedBits,
            double percentageFlipped
    ) {}

    public static AvalancheAnalysis evaluateAvalanche(String originalText, HashType type) {
        if (originalText == null || originalText.isEmpty()) {
            originalText = "CipheRaz";
        }

        // Create modified string by flipping the least significant bit of the first byte
        byte[] originalBytes = originalText.getBytes(StandardCharsets.UTF_8);
        byte[] modifiedBytes = originalBytes.clone();
        modifiedBytes[0] = (byte) (modifiedBytes[0] ^ 0x01); // flip 1 bit
        String modifiedText = new String(modifiedBytes, StandardCharsets.UTF_8);

        byte[] digest1 = hash(originalText, type);
        byte[] digest2 = hash(modifiedText, type);

        int totalBits = digest1.length * 8;
        int flippedBits = 0;

        for (int i = 0; i < digest1.length; i++) {
            int xor = (digest1[i] ^ digest2[i]) & 0xFF;
            flippedBits += Integer.bitCount(xor);
        }

        double percentage = (flippedBits * 100.0) / totalBits;

        return new AvalancheAnalysis(
                originalText,
                modifiedText,
                HexFormat.of().formatHex(digest1),
                HexFormat.of().formatHex(digest2),
                totalBits,
                flippedBits,
                percentage
        );
    }
}
