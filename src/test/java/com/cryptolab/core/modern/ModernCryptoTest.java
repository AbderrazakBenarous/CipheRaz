package com.cryptolab.core.modern;

import com.cryptolab.core.hashing.HashEngine;
import com.cryptolab.core.model.CryptoResult;
import com.cryptolab.core.signatures.DigitalSignatureEngine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.security.KeyPair;
import java.util.HexFormat;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive test suite validating modern symmetric and asymmetric cryptography,
 * hash algorithms, Avalanche Effect, and digital signatures with tamper verification.
 *
 * Developed by Abderrazak BENAROUS
 */
class ModernCryptoTest {

    @Test
    @DisplayName("AES-GCM (256-bit): Round-trip and authentication tag tamper detection")
    void testAesGcm() {
        AesEngine aes = new AesEngine();
        byte[] key = AesEngine.generateRandomKey(256);
        byte[] iv = AesEngine.generateRandomIv(12);
        String plaintext = "TOP SECRET UNIVERSITY CYBERSECURITY LAB EXPERIMENT";

        // Encrypt
        CryptoResult enc = aes.encrypt(plaintext, key, AesEngine.Mode.GCM, iv);
        assertThat(enc.success()).isTrue();
        assertThat(enc.outputText()).contains("IV (Hex):");

        // Extract ciphertext hex
        String raw = enc.outputText();
        int start = raw.indexOf("Ciphertext (Hex): ") + "Ciphertext (Hex): ".length();
        int end = raw.indexOf("\n", start);
        String cipherHex = raw.substring(start, end).trim();
        byte[] cipherBytes = HexFormat.of().parseHex(cipherHex);

        // Decrypt successfully
        CryptoResult dec = aes.decrypt(cipherBytes, key, AesEngine.Mode.GCM, iv);
        assertThat(dec.success()).isTrue();
        assertThat(dec.outputText()).isEqualTo(plaintext);

        // Tamper test: flip 1 byte of the ciphertext/tag -> must fail authentication
        byte[] tamperedCipher = cipherBytes.clone();
        tamperedCipher[tamperedCipher.length - 1] ^= 0x01;
        CryptoResult failDec = aes.decrypt(tamperedCipher, key, AesEngine.Mode.GCM, iv);
        assertThat(failDec.success()).isFalse();
        assertThat(failDec.errorMessage()).contains("AUTHENTICATION FAILED");
    }

    @Test
    @DisplayName("AES-CBC (128-bit): Round-trip with PKCS5 padding")
    void testAesCbc() {
        AesEngine aes = new AesEngine();
        byte[] key = AesEngine.generateRandomKey(128);
        byte[] iv = AesEngine.generateRandomIv(16);
        String plaintext = "CBC Mode Message Requiring Padding Bytes.";

        CryptoResult enc = aes.encrypt(plaintext, key, AesEngine.Mode.CBC, iv);
        assertThat(enc.success()).isTrue();

        String raw = enc.outputText();
        int start = raw.indexOf("Ciphertext (Hex): ") + "Ciphertext (Hex): ".length();
        int end = raw.indexOf("\n", start);
        String cipherHex = raw.substring(start, end).trim();
        byte[] cipherBytes = HexFormat.of().parseHex(cipherHex);

        CryptoResult dec = aes.decrypt(cipherBytes, key, AesEngine.Mode.CBC, iv);
        assertThat(dec.success()).isTrue();
        assertThat(dec.outputText()).isEqualTo(plaintext);
    }

    @Test
    @DisplayName("Educational RSA: mathematical primes, totient, and modular exponentiation")
    void testEducationalRsa() {
        RsaEngine rsa = new RsaEngine();
        RsaEngine.EducationalKeyParameters params = RsaEngine.generateEducationalKeys(16);

        assertThat(params.p().isProbablePrime(100)).isTrue();
        assertThat(params.q().isProbablePrime(100)).isTrue();
        assertThat(params.n()).isEqualTo(params.p().multiply(params.q()));
        assertThat(params.e().gcd(params.phi())).isEqualTo(BigInteger.ONE);
        assertThat(params.e().multiply(params.d()).mod(params.phi())).isEqualTo(BigInteger.ONE);

        BigInteger message = BigInteger.valueOf(12345);
        CryptoResult enc = rsa.encryptEducational(message, params);
        assertThat(enc.success()).isTrue();

        BigInteger cipherVal = new BigInteger(enc.outputText());
        CryptoResult dec = rsa.decryptEducational(cipherVal, params);
        assertThat(dec.success()).isTrue();
        assertThat(new BigInteger(dec.outputText())).isEqualTo(message);
    }

    @Test
    @DisplayName("Production RSA: 2048-bit key pair generation and OAEP encryption/decryption")
    void testProductionRsa() throws Exception {
        RsaEngine rsa = new RsaEngine();
        KeyPair keyPair = RsaEngine.generateProductionKeyPair(2048);
        String plaintext = "High security dispatch using RSA-OAEP with SHA-256";

        CryptoResult enc = rsa.encryptProduction(plaintext, keyPair.getPublic());
        assertThat(enc.success()).isTrue();

        CryptoResult dec = rsa.decryptProduction(enc.outputText(), keyPair.getPrivate());
        assertThat(dec.success()).isTrue();
        assertThat(dec.outputText()).isEqualTo(plaintext);
    }

    @Test
    @DisplayName("Diffie-Hellman Key Exchange: Alice and Bob derive identical shared secret")
    void testDiffieHellman() {
        DiffieHellmanEngine dh = new DiffieHellmanEngine();
        var result = dh.simulateExchange(24);

        assertThat(result.secretsMatch()).isTrue();
        assertThat(result.sharedSecretAlice()).isEqualTo(result.sharedSecretBob());
        assertThat(result.protocolSteps()).hasSize(7);
    }

    @Test
    @DisplayName("Hash functions: standard test vectors and Avalanche Effect analysis")
    void testHashEngine() {
        // Known NIST vector: SHA-256("abc") = ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad
        String sha256Abc = HashEngine.hashToHex("abc", HashEngine.HashType.SHA256);
        assertThat(sha256Abc).isEqualTo("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad");

        // Avalanche Effect: 1 bit change flips ~40% to ~60% of bits
        var avalanche = HashEngine.evaluateAvalanche("University Cybersecurity Laboratory 2026", HashEngine.HashType.SHA256);
        assertThat(avalanche.totalDigestBits()).isEqualTo(256);
        assertThat(avalanche.percentageFlipped()).isBetween(35.0, 65.0);
    }

    @Test
    @DisplayName("Digital Signatures: RSA and ECDSA signing and tamper rejection")
    void testDigitalSignatures() throws Exception {
        for (DigitalSignatureEngine.SignatureScheme scheme : DigitalSignatureEngine.SignatureScheme.values()) {
            KeyPair keyPair = DigitalSignatureEngine.generateKeyPair(scheme);
            String message = "Authorize payment of $500.00 to Student Research Fund";

            // Sign
            var signRes = DigitalSignatureEngine.sign(message, keyPair.getPrivate(), scheme);
            assertThat(signRes.signatureBytes()).isNotEmpty();
            assertThat(signRes.base64Signature()).isNotEmpty();

            // Verify with unaltered message
            var verifyValid = DigitalSignatureEngine.verify(message, signRes.signatureBytes(), keyPair.getPublic(), scheme);
            assertThat(verifyValid.valid()).isTrue();
            assertThat(verifyValid.statusMessage()).contains("SIGNATURE VALID");

            // Verify with tampered message
            String tamperedMessage = "Authorize payment of $50,000 to Student Research Fund";
            var verifyTampered = DigitalSignatureEngine.verify(tamperedMessage, signRes.signatureBytes(), keyPair.getPublic(), scheme);
            assertThat(verifyTampered.valid()).isFalse();
            assertThat(verifyTampered.statusMessage()).contains("VERIFICATION FAILED");
        }
    }
}
