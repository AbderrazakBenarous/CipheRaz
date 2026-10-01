package com.cryptolab.core.modern;

import com.cryptolab.core.model.AlgorithmCategory;
import com.cryptolab.core.model.CryptoAlgorithm;
import com.cryptolab.core.model.SecurityStatus;
import com.cryptolab.core.security.SecureRandomProvider;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Educational implementation of the Diffie-Hellman Key Exchange Protocol.
 * Demonstrates how two parties establish a shared secret over an insecure channel.
 *
 * Developed by Abderrazak BENAROUS
 */
public class DiffieHellmanEngine implements CryptoAlgorithm {

    @Override
    public String getId() {
        return "diffie-hellman";
    }

    @Override
    public String getName() {
        return "Diffie-Hellman Key Exchange";
    }

    @Override
    public String getDescription() {
        return "Foundational cryptographic protocol enabling two parties to establish a shared secret over an unsecure channel.";
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
        return "Diffie-Hellman relies on the hardness of the Discrete Logarithm Problem (DLP). " +
                "Even though an eavesdropper observes p, g, A (Alice's public key), and B (Bob's public key), " +
                "computing the shared secret g^(ab) mod p is computationally infeasible without knowing private a or b.";
    }

    public record DhExchangeResult(
            BigInteger p,
            BigInteger g,
            BigInteger privateAlice,
            BigInteger publicAlice,
            BigInteger privateBob,
            BigInteger publicBob,
            BigInteger sharedSecretAlice,
            BigInteger sharedSecretBob,
            boolean secretsMatch,
            List<String> protocolSteps
    ) {}

    public DhExchangeResult simulateExchange(int primeBits) {
        // Generate public parameters
        BigInteger p = BigInteger.probablePrime(primeBits, SecureRandomProvider.getSecureRandom());
        BigInteger g = BigInteger.valueOf(2); // Typical generator

        // Alice picks private a
        BigInteger a = new BigInteger(primeBits - 2, SecureRandomProvider.getSecureRandom()).add(BigInteger.TWO);
        BigInteger A = g.modPow(a, p);

        // Bob picks private b
        BigInteger b = new BigInteger(primeBits - 2, SecureRandomProvider.getSecureRandom()).add(BigInteger.TWO);
        BigInteger B = g.modPow(b, p);

        // Shared secret computation
        BigInteger secretAlice = B.modPow(a, p);
        BigInteger secretBob = A.modPow(b, p);

        List<String> steps = new ArrayList<>();
        steps.add("Step 1 (Public Agreement): Prime p = " + p + ", Generator g = " + g);
        steps.add("Step 2 (Alice Private): a = " + a + " -> Computes Public A = g^a mod p = " + A);
        steps.add("Step 3 (Bob Private): b = " + b + " -> Computes Public B = g^b mod p = " + B);
        steps.add("Step 4 (Public Exchange): Alice transmits A to Bob; Bob transmits B to Alice (eavesdropper sees p, g, A, B)");
        steps.add("Step 5 (Alice Derivation): Secret = B^a mod p = (" + B + "^" + a + ") mod " + p + " = " + secretAlice);
        steps.add("Step 6 (Bob Derivation): Secret = A^b mod p = (" + A + "^" + b + ") mod " + p + " = " + secretBob);
        steps.add("Step 7 (Verification): Shared secrets match: " + secretAlice.equals(secretBob));

        return new DhExchangeResult(p, g, a, A, b, B, secretAlice, secretBob, secretAlice.equals(secretBob), steps);
    }
}
