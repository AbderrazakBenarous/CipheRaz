package com.cryptolab.educational.exercises;

import com.cryptolab.core.model.AlgorithmCategory;
import com.cryptolab.core.model.DifficultyLevel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages coursework challenges and evaluates student laboratory submissions.
 *
 * Developed by Abderrazak BENAROUS
 */
public class ExerciseManager {

    private final List<LabExercise> exercises = new ArrayList<>();

    public ExerciseManager() {
        loadDefaultExercises();
    }

    private void loadDefaultExercises() {
        exercises.add(new LabExercise(
                "EX-01",
                "Caesar Ciphertext Decryption",
                AlgorithmCategory.CLASSICAL,
                DifficultyLevel.BEGINNER,
                "Decrypt the following ciphertext intercepted from an ancient military dispatch. Determine the numerical shift k and recover the plaintext message.",
                "WKLV LV D VHFUHW FDHVDU PHVVDJH",
                "THIS IS A SECRET CAESAR MESSAGE",
                "By shifting backward by k = 3 (or testing all 25 shifts using the Cryptanalysis Lab), the original plaintext is revealed: 'THIS IS A SECRET CAESAR MESSAGE'.",
                20
        ));

        exercises.add(new LabExercise(
                "EX-02",
                "Atbash Symmetric Inversion",
                AlgorithmCategory.CLASSICAL,
                DifficultyLevel.BEGINNER,
                "The Hebrew Atbash cipher mirrors each character across the Latin alphabet (A<->Z, B<->Y, C<->X). Decipher the target ciphertext word.",
                "XRKSVIZA",
                "CIPHERAZ",
                "Applying Atbash inversion: X->C, R->I, K->P, S->H, V->E, I->R, Z->A, A->Z. Result = 'CIPHERAZ'.",
                20
        ));

        exercises.add(new LabExercise(
                "EX-03",
                "RSA Private Exponent Derivation",
                AlgorithmCategory.ASYMMETRIC,
                DifficultyLevel.INTERMEDIATE,
                "Given RSA prime parameters p = 61 and q = 53, with public exponent e = 17.\n" +
                "Calculate Euler's totient phi(n) and the private decryption exponent d = e^(-1) mod phi(n). Submit the numerical value of d.",
                "p = 61, q = 53, e = 17",
                "2753",
                "Step 1: phi(n) = (61 - 1) * (53 - 1) = 60 * 52 = 3120.\n" +
                "Step 2: Extended Euclidean Algorithm finds d such that (17 * d) mod 3120 = 1.\n" +
                "Step 3: d = 2753 (since 17 * 2753 = 46801 = 15 * 3120 + 1).",
                25
        ));

        exercises.add(new LabExercise(
                "EX-04",
                "Steganography Image Capacity Calculation",
                AlgorithmCategory.STEGANOGRAPHY,
                DifficultyLevel.INTERMEDIATE,
                "A digital forensics student is provided a 800x600 lossless PNG cover image using 3 color channels (Red, Green, Blue). " +
                "If the embedding engine is configured to use 2 bits per channel, what is the maximum payload capacity in bytes? Submit the exact integer byte count.",
                "Image dimensions: 800x600 pixels • Channels: 3 (RGB) • Bit depth: 2 bits/channel",
                "360000",
                "Capacity Formula: (Width * Height * Channels * BitsPerChannel) / 8\n" +
                "= (800 * 600 * 3 * 2) / 8 = 2,880,000 bits / 8 = 360,000 bytes (360 KB).",
                20
        ));

        exercises.add(new LabExercise(
                "EX-05",
                "Cryptographic Hash Integrity Verification",
                AlgorithmCategory.HASHING,
                DifficultyLevel.ADVANCED,
                "What is the standard output digest length in bits for the SHA-256 cryptographic hash function?",
                "NIST FIPS 180-4 Secure Hash Standard (SHA-256)",
                "256",
                "SHA-256 produces a fixed 256-bit (32-byte) digest regardless of input message length, offering 128-bit collision resistance.",
                15
        ));
    }

    public List<LabExercise> getExercises() {
        return Collections.unmodifiableList(exercises);
    }

    public LabExercise findById(String id) {
        return exercises.stream().filter(e -> e.id().equalsIgnoreCase(id)).findFirst().orElse(null);
    }
}
