package com.cryptolab.core.model;

/**
 * Security advisory status representing real-world viability of an algorithm.
 * Crucial for university education so students do not confuse historic/toy algorithms with modern security.
 *
 * Developed by Abderrazak BENAROUS
 */
public enum SecurityStatus {
    SECURE_RECOMMENDED(
            "Production Secure",
            "Approved standard for modern confidential systems (e.g., AES-256-GCM, SHA-256, RSA-3072+).",
            "#10B981"
    ),
    DEPRECATED(
            "Deprecated / Insecure",
            "Vulnerable to practical attacks or collisions. Retained only for historical and legacy educational analysis (e.g., MD5, SHA-1, DES).",
            "#F59E0B"
    ),
    CLASSICAL_BROKEN(
            "Classical / Broken",
            "Easily broken via frequency analysis or trivial brute force. Strictly for educational demonstration of cipher evolution.",
            "#EF4444"
    ),
    EDUCATIONAL_ONLY(
            "Educational Math Toy",
            "Uses reduced key spaces or toy parameters to demonstrate mathematical concepts step-by-step.",
            "#818CF8"
    );

    private final String title;
    private final String advisory;
    private final String colorHex;

    SecurityStatus(String title, String advisory, String colorHex) {
        this.title = title;
        this.advisory = advisory;
        this.colorHex = colorHex;
    }

    public String getTitle() {
        return title;
    }

    public String getAdvisory() {
        return advisory;
    }

    public String getColorHex() {
        return colorHex;
    }
}
