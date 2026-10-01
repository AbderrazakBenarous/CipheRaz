package com.cryptolab.core.model;

/**
 * Educational difficulty modes catering from beginner students to advanced researchers.
 *
 * Developed by Abderrazak BENAROUS
 */
public enum DifficultyLevel {
    BEGINNER("Beginner", "Simplified inputs, intuitive visualizers, and conceptual explanations"),
    INTERMEDIATE("Intermediate", "Configurable cryptographic parameters, block details, and intermediate steps"),
    ADVANCED("Advanced", "Full mathematical proofs, entropy calculations, raw bit/hex inspections, and attack simulations");

    private final String label;
    private final String description;

    DifficultyLevel(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }
}
