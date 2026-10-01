package com.cryptolab.ui.views.learning;

import com.cryptolab.app.NavigationManager;
import com.cryptolab.app.NavigationManager.ViewId;
import com.cryptolab.ui.components.SvgIcons;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

/**
 * Interactive Algorithm Explorer & Cybersecurity Concept Guides.
 *
 * Developed by Abderrazak BENAROUS
 */
public class LearningExplorerView extends ScrollPane {

    private final NavigationManager navigationManager;

    public LearningExplorerView(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;

        setFitToWidth(true);
        setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-padding: 0;");

        VBox root = new VBox(20);
        root.getStyleClass().add("content-area");

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Algorithm Explorer & Foundational Concepts");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #F9FAFB;");
        header.getChildren().addAll(SvgIcons.createIcon(SvgIcons.ICON_BOOK, 24, "#38BDF8"), title);

        VBox guides = new VBox(16);

        guides.getChildren().addAll(
                createConceptCard(
                        "1. Evolution of Ciphers: Monoalphabetic to Polyalphabetic",
                        "From Caesar's single-shift substitution to Vigenère's cyclic polyalphabetic tableau, ciphers evolved to counter single-letter frequency attacks. " +
                        "However, any periodic polyalphabetic cipher can be unmasked by Kasiski examination and the Index of Coincidence (IoC).",
                        "Launch Classical Lab",
                        () -> navigationManager.navigateTo(ViewId.CLASSICAL)
                ),
                createConceptCard(
                        "2. Modern Symmetric Block Ciphers: AES & AEAD Modes",
                        "AES (Rijndael) operates on 128-bit blocks using Substitution-Permutation Networks (SPN). " +
                        "While CBC mode provides confidentiality, Galois/Counter Mode (GCM) combines CTR encryption with GMAC authentication, " +
                        "providing Authenticated Encryption with Associated Data (AEAD). Nonce reuse in GCM completely compromises security.",
                        "Launch Modern Lab (AES)",
                        () -> navigationManager.navigateTo(ViewId.MODERN)
                ),
                createConceptCard(
                        "3. Asymmetric Cryptography: Trapdoor One-Way Functions",
                        "Public-key systems (RSA, Diffie-Hellman, ECC) rely on mathematical asymmetry: operations easy to compute in one direction but intractable to reverse without a trapdoor key. " +
                        "RSA exploits prime factorization (n = p*q), while Diffie-Hellman relies on the Discrete Logarithm Problem (DLP).",
                        "Launch RSA & DH Lab",
                        () -> navigationManager.navigateTo(ViewId.MODERN)
                ),
                createConceptCard(
                        "4. Steganography vs Encryption: Concealment vs Privacy",
                        "Encryption conceals content (rendering it unreadable cipher text), but signals the existence of communication to adversaries. " +
                        "Steganography conceals the existence of communication within innocent cover media (PNG/BMP pixel LSBs). " +
                        "Optimal operational security combines both: AES-GCM encrypted payloads embedded into high-capacity cover rasters.",
                        "Launch Steganography Lab",
                        () -> navigationManager.navigateTo(ViewId.STEGANOGRAPHY)
                ),
                createConceptCard(
                        "5. Cryptanalysis & Practical Attack Demonstrations",
                        "Cryptanalysis discovers weaknesses in mathematical structure and implementation flaws. " +
                        "Chi-square tests compare observed letter frequencies against natural language distributions, " +
                        "allowing instant automated decipherment of monoalphabetic keys.",
                        "Launch Cryptanalysis Lab",
                        () -> navigationManager.navigateTo(ViewId.CRYPTANALYSIS)
                )
        );

        root.getChildren().addAll(header, guides);
        setContent(root);
    }

    private VBox createConceptCard(String title, String contentText, String actionBtnText, Runnable onAction) {
        VBox card = new VBox(10);
        card.getStyleClass().add("lab-card");

        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER_LEFT);

        Label t = new Label(title);
        t.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #38BDF8;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btn = new Button(actionBtnText);
        btn.getStyleClass().add("btn-secondary");
        btn.setOnAction(e -> onAction.run());

        top.getChildren().addAll(t, spacer, btn);

        Label body = new Label(contentText);
        body.setWrapText(true);
        body.setStyle("-fx-font-size: 13px; -fx-text-fill: #CBD5E1; -fx-line-spacing: 3;");

        card.getChildren().addAll(top, body);
        return card;
    }
}
