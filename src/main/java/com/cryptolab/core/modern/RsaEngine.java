package com.cryptolab.core.modern;

import com.cryptolab.core.model.AlgorithmCategory;
import com.cryptolab.core.model.CryptoAlgorithm;
import com.cryptolab.core.model.CryptoResult;
import com.cryptolab.core.model.SecurityStatus;
import com.cryptolab.core.security.SecureRandomProvider;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Dual-mode RSA engine:
 * 1. Educational Math Mode: Small primes, explicit p, q, n, phi(n), e, d calculations.
 * 2. Production JCA Mode: Secure 2048/3072/4096-bit key pair generation with OAEP padding.
 *
 * Developed by Abderrazak BENAROUS
 */
public class RsaEngine implements CryptoAlgorithm {

    @Override
    public String getId() {
        return "rsa";
    }

    @Override
    public String getName() {
        return "RSA Public-Key Cryptosystem";
    }

    @Override
    public String getDescription() {
        return "Asymmetric cryptosystem based on the practical difficulty of the prime factorization problem.";
    }

    @Override
    public AlgorithmCategory getCategory() {
        return AlgorithmCategory.ASYMMETRIC;
    }

    @Override
    public SecurityStatus getSecurityStatus() {
        return SecurityStatus.SECURE_RECOMMENDED;
    }

    @Override
    public String getEducationalSummary() {
        return "RSA security depends on the hardness of factoring n = p * q. In educational mode, small primes allow students " +
                "to trace Euler's totient phi(n) and the extended Euclidean modular inverse d = e^(-1) mod phi(n). " +
                "WARNING: Educational small keys can be factored in milliseconds. Modern security mandates 2048-bit or 3072-bit keys with OAEP padding.";
    }

    // -------------------------------------------------------------
    // 1. Educational Math Mode (Small Primes / BigInteger)
    // -------------------------------------------------------------
    public record EducationalKeyParameters(
            BigInteger p,
            BigInteger q,
            BigInteger n,
            BigInteger phi,
            BigInteger e,
            BigInteger d
    ) {}

    public static EducationalKeyParameters generateEducationalKeys(int bitLength) {
        SecureRandom random = SecureRandomProvider.getSecureRandom();
        BigInteger p = BigInteger.probablePrime(bitLength, random);
        BigInteger q;
        do {
            q = BigInteger.probablePrime(bitLength, random);
        } while (p.equals(q));

        BigInteger n = p.multiply(q);
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        // Common public exponent e = 65537, or fallback to small coprime
        BigInteger e = BigInteger.valueOf(65537);
        if (e.compareTo(phi) >= 0 || !e.gcd(phi).equals(BigInteger.ONE)) {
            e = BigInteger.valueOf(3);
            while (!e.gcd(phi).equals(BigInteger.ONE)) {
                e = e.add(BigInteger.TWO);
            }
        }

        BigInteger d = e.modInverse(phi);
        return new EducationalKeyParameters(p, q, n, phi, e, d);
    }

    public CryptoResult encryptEducational(BigInteger messageInt, EducationalKeyParameters params) {
        long startTime = System.nanoTime();
        if (messageInt.compareTo(params.n()) >= 0) {
            return CryptoResult.failure("Message integer (" + messageInt + ") must be strictly less than modulus n (" + params.n() + ")");
        }

        BigInteger c = messageInt.modPow(params.e(), params.n());
        long duration = System.nanoTime() - startTime;

        List<String> traces = new ArrayList<>();
        traces.add("1. Modulus n = p * q = " + params.p() + " * " + params.q() + " = " + params.n());
        traces.add("2. Totient phi(n) = (p-1)*(q-1) = " + params.phi());
        traces.add("3. Public Exponent e = " + params.e() + " (gcd(e, phi) = 1)");
        traces.add("4. Private Exponent d = e^(-1) mod phi = " + params.d());
        traces.add(String.format("5. Encryption formula: c = m^e mod n = %s^%s mod %s = %s",
                messageInt, params.e(), params.n(), c));

        String explanation = "Educational RSA encryption: c = " + c + ". Educational toy parameters only!";
        return CryptoResult.ok(c.toString(), explanation, traces, duration);
    }

    public CryptoResult decryptEducational(BigInteger cipherInt, EducationalKeyParameters params) {
        long startTime = System.nanoTime();
        BigInteger m = cipherInt.modPow(params.d(), params.n());
        long duration = System.nanoTime() - startTime;

        List<String> traces = new ArrayList<>();
        traces.add(String.format("1. Decryption formula: m = c^d mod n = %s^%s mod %s = %s",
                cipherInt, params.d(), params.n(), m));
        traces.add("2. Decrypted numerical value recovered: " + m);

        String explanation = "Educational RSA decryption: m = " + m;
        return CryptoResult.ok(m.toString(), explanation, traces, duration);
    }

    // -------------------------------------------------------------
    // 2. Production JCA Mode (2048+ bits, OAEP Padding)
    // -------------------------------------------------------------
    public static KeyPair generateProductionKeyPair(int keyBits) throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(keyBits, SecureRandomProvider.getSecureRandom());
        return generator.generateKeyPair();
    }

    public CryptoResult encryptProduction(String plaintext, PublicKey publicKey) {
        long startTime = System.nanoTime();
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] cipherBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            String base64Cipher = Base64.getEncoder().encodeToString(cipherBytes);

            List<String> traces = List.of(
                    "Algorithm: RSA with OAEP (Optimal Asymmetric Encryption Padding)",
                    "Digest: SHA-256, MGF: MGF1",
                    "Ciphertext Size: " + cipherBytes.length + " bytes (" + (cipherBytes.length * 8) + " bits)"
            );

            long duration = System.nanoTime() - startTime;
            return CryptoResult.ok(base64Cipher, "Production RSA-OAEP encryption successful.", traces, duration);
        } catch (Exception e) {
            return CryptoResult.failure("RSA-OAEP encryption failed: " + e.getMessage());
        }
    }

    public CryptoResult decryptProduction(String base64Ciphertext, PrivateKey privateKey) {
        long startTime = System.nanoTime();
        try {
            byte[] cipherBytes = Base64.getDecoder().decode(base64Ciphertext.trim());
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] plainBytes = cipher.doFinal(cipherBytes);
            String plainText = new String(plainBytes, StandardCharsets.UTF_8);

            List<String> traces = List.of(
                    "OAEP Padding Unmasked & Verified",
                    "Recovered Plaintext Size: " + plainBytes.length + " bytes"
            );

            long duration = System.nanoTime() - startTime;
            return CryptoResult.ok(plainText, "Production RSA-OAEP decryption successful.", traces, duration);
        } catch (Exception e) {
            return CryptoResult.failure("RSA-OAEP decryption failed: " + e.getMessage());
        }
    }
}
