package com.cryptolab.core.classical;

import com.cryptolab.core.model.CryptoResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive test suite for classical cipher algorithms.
 * Validates round-trip correctness, edge cases, invalid key handling, and step traces.
 *
 * Developed by Abderrazak BENAROUS
 */
class ClassicalCiphersTest {

    @Test
    @DisplayName("Caesar cipher: round-trip, shift wrapping, and case preservation")
    void testCaesarCipher() {
        CaesarCipher cipher = new CaesarCipher();
        String original = "Hello, World! Attack at Dawn 123.";

        // Shift 3
        CryptoResult enc = cipher.encrypt(original, 3);
        assertThat(enc.success()).isTrue();
        assertThat(enc.outputText()).isEqualTo("Khoor, Zruog! Dwwdfn dw Gdzq 123.");
        assertThat(enc.stepTraces()).isNotEmpty();

        CryptoResult dec = cipher.decrypt(enc.outputText(), 3);
        assertThat(dec.success()).isTrue();
        assertThat(dec.outputText()).isEqualTo(original);

        // Shift > 26 (wrapping)
        CryptoResult encWrap = cipher.encrypt(original, 29); // 29 mod 26 = 3
        assertThat(encWrap.outputText()).isEqualTo(enc.outputText());

        // Negative shift
        CryptoResult encNeg = cipher.encrypt(original, -1);
        CryptoResult decNeg = cipher.decrypt(encNeg.outputText(), -1);
        assertThat(decNeg.outputText()).isEqualTo(original);

        // Empty input
        assertThat(cipher.encrypt("", 5).outputText()).isEmpty();
    }

    @Test
    @DisplayName("Atbash cipher: involution property and reflection")
    void testAtbashCipher() {
        AtbashCipher cipher = new AtbashCipher();
        String original = "Cryptography & Steganography 2026";

        CryptoResult pass1 = cipher.process(original);
        assertThat(pass1.success()).isTrue();
        assertThat(pass1.outputText()).doesNotContain("Cryptography");

        // Involution: running twice yields the exact original text
        CryptoResult pass2 = cipher.process(pass1.outputText());
        assertThat(pass2.success()).isTrue();
        assertThat(pass2.outputText()).isEqualTo(original);
    }

    @Test
    @DisplayName("Affine cipher: round-trip and non-coprime key rejection")
    void testAffineCipher() {
        AffineCipher cipher = new AffineCipher();
        String original = "SECRET MESSAGE TO INTERCEPT";

        // Valid coprime parameters: a = 5, b = 8
        CryptoResult enc = cipher.encrypt(original, 5, 8);
        assertThat(enc.success()).isTrue();
        assertThat(enc.outputText()).isNotEqualTo(original);

        CryptoResult dec = cipher.decrypt(enc.outputText(), 5, 8);
        assertThat(dec.success()).isTrue();
        assertThat(dec.outputText()).isEqualTo(original);

        // Invalid key: a = 4 is not coprime to 26 (gcd = 2)
        CryptoResult invalid = cipher.encrypt(original, 4, 8);
        assertThat(invalid.success()).isFalse();
        assertThat(invalid.errorMessage()).contains("not coprime with 26");

        // Invalid key: a = 13 (gcd = 13)
        CryptoResult invalid13 = cipher.encrypt(original, 13, 8);
        assertThat(invalid13.success()).isFalse();
    }

    @Test
    @DisplayName("Vigenère cipher: cyclic keyword polyalphabetic encryption and decryption")
    void testVigenereCipher() {
        VigenereCipher cipher = new VigenereCipher();
        String original = "DEFEND THE EAST WALL OF THE CASTLE";
        String keyword = "LEMON";

        CryptoResult enc = cipher.encrypt(original, keyword);
        assertThat(enc.success()).isTrue();
        // D(3)+L(11)=O(14), E(4)+E(4)=I(8), F(5)+M(12)=R(17), E(4)+O(14)=S(18), N(13)+N(13)=A(0), D(3)+L(11)=O(14)
        assertThat(enc.outputText()).startsWith("OIRSAO");

        CryptoResult dec = cipher.decrypt(enc.outputText(), keyword);
        assertThat(dec.success()).isTrue();
        assertThat(dec.outputText()).isEqualTo(original);

        // Empty keyword validation
        CryptoResult failEmpty = cipher.encrypt(original, "   ");
        assertThat(failEmpty.success()).isFalse();
        assertThat(failEmpty.errorMessage()).contains("Keyword cannot be empty");
    }

    @Test
    @DisplayName("Playfair cipher: 5x5 matrix digraph substitution")
    void testPlayfairCipher() {
        PlayfairCipher cipher = new PlayfairCipher();
        String original = "INSTRUMENTS";
        String keyword = "MONARCHY";

        CryptoResult enc = cipher.encrypt(original, keyword);
        assertThat(enc.success()).isTrue();
        assertThat(enc.outputText()).isNotEmpty();

        CryptoResult dec = cipher.decrypt(enc.outputText(), keyword);
        assertThat(dec.success()).isTrue();
        // Since odd-length or digram rules may append filler 'X', check prefix
        assertThat(dec.outputText()).startsWith("INSTRUMENTS");
    }

    @Test
    @DisplayName("Rail Fence cipher: zigzag transposition round-trip across various rail depths")
    void testRailFenceCipher() {
        RailFenceCipher cipher = new RailFenceCipher();
        String original = "DEFEND THE EAST WALL AT DAWN";

        for (int rails = 2; rails <= 6; rails++) {
            CryptoResult enc = cipher.encrypt(original, rails);
            assertThat(enc.success()).isTrue();

            CryptoResult dec = cipher.decrypt(enc.outputText(), rails);
            assertThat(dec.success()).isTrue();
            assertThat(dec.outputText()).isEqualTo(original);
        }

        // Trivial rails edge cases
        assertThat(cipher.encrypt(original, 1).outputText()).isEqualTo(original);
        assertThat(cipher.encrypt(original, 100).outputText()).isEqualTo(original);
    }

    @Test
    @DisplayName("Simple Substitution cipher: arbitrary permutation and invalid key checks")
    void testSimpleSubstitutionCipher() {
        SimpleSubstitutionCipher cipher = new SimpleSubstitutionCipher();
        String original = "ATTACK THE NORTHERN BRIDGE AT MIDNIGHT";
        String validKey = "QWERTYUIOPASDFGHJKLZXCVBNM";

        CryptoResult enc = cipher.encrypt(original, validKey);
        assertThat(enc.success()).isTrue();
        assertThat(enc.outputText()).isNotEqualTo(original);

        CryptoResult dec = cipher.decrypt(enc.outputText(), validKey);
        assertThat(dec.success()).isTrue();
        assertThat(dec.outputText()).isEqualTo(original);

        // Invalid key: duplicate letters (e.g. two 'A's)
        String duplicateKey = "QWERTYUIOPASDFGHJKLZXCVBNA";
        CryptoResult failDup = cipher.encrypt(original, duplicateKey);
        assertThat(failDup.success()).isFalse();
        assertThat(failDup.errorMessage()).contains("must contain exactly 26 unique English alphabet letters");
    }
}
