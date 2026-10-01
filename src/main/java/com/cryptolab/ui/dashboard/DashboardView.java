package com.cryptolab.ui.dashboard;

import com.cryptolab.app.NavigationManager;
import com.cryptolab.app.NavigationManager.ViewId;
import com.cryptolab.core.model.AlgorithmCategory;
import com.cryptolab.core.model.SecurityStatus;
import com.cryptolab.ui.components.LabCard;
import com.cryptolab.ui.components.SecurityNoticeBanner;
import com.cryptolab.ui.components.SvgIcons;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Main dashboard view for CryptoLab presenting laboratory modules,
 * live metrics, and educational advisories.
 *
 * Developed by Abderrazak BENAROUS
 */
public class DashboardView extends ScrollPane {

    private final NavigationManager navigationManager;

    public DashboardView(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;

        setFitToWidth(true);
        setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-padding: 0;");

        VBox content = new VBox(22);
        content.getStyleClass().add("content-area");

        // 1. Hero Section
        content.getChildren().add(createHeroSection());

        // 2. Security Notice Banner
        content.getChildren().add(new SecurityNoticeBanner(
                SecurityStatus.EDUCATIONAL_ONLY,
                "University Laboratory Notice: Classical ciphers and toy RSA parameters are provided strictly for mathematical instruction and cryptanalysis exercises. Never deploy classical ciphers or short keys for production security."
        ));

        // 3. Quick Stats Bar
        content.getChildren().add(createStatsBar());

        // 4. Laboratory Cards Grid
        Label sectionTitle = new Label("LABORATORY SUITES");
        sectionTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #94A3B8;");
        content.getChildren().add(sectionTitle);
        content.getChildren().add(createLabGrid());

        setContent(content);
    }

    private VBox createHeroSection() {
        VBox hero = new VBox(12);
        hero.getStyleClass().add("hero-box");

        Label title = new Label("Cybersecurity & Cryptography Research Laboratory");
        title.getStyleClass().add("hero-title");

        Label subtitle = new Label(
                "Welcome to CipheRaz. Explore algorithmic mechanics, analyze cipher vulnerability through practical cryptanalysis, " +
                "and inspect bit-level pixel manipulation in digital steganography."
        );
        subtitle.getStyleClass().add("hero-subtitle");

        HBox actions = new HBox(12);
        actions.setAlignment(Pos.CENTER_LEFT);

        Button startBtn = new Button("Launch Classical Lab");
        startBtn.getStyleClass().add("btn-primary");
        startBtn.setGraphic(SvgIcons.createIcon(SvgIcons.ICON_PLAY, 14, "#0B0F19"));
        startBtn.setOnAction(e -> navigationManager.navigateTo(ViewId.CLASSICAL));

        Button stegoBtn = new Button("Explore Steganography");
        stegoBtn.getStyleClass().add("btn-secondary");
        stegoBtn.setGraphic(SvgIcons.createIcon(SvgIcons.ICON_EYE, 14, "#38BDF8"));
        stegoBtn.setOnAction(e -> navigationManager.navigateTo(ViewId.STEGANOGRAPHY));

        actions.getChildren().addAll(startBtn, stegoBtn);
        hero.getChildren().addAll(title, subtitle, actions);
        return hero;
    }

    private HBox createStatsBar() {
        HBox statsBar = new HBox(16);
        statsBar.setAlignment(Pos.CENTER_LEFT);

        statsBar.getChildren().addAll(
                createStatBox("6 Active Suites", "Interactive Cybersecurity Modules", SvgIcons.ICON_DASHBOARD),
                createStatBox("14+ Algorithms", "Classical, Modern, Hash, Signatures", SvgIcons.ICON_LOCK),
                createStatBox("100% Offline", "Zero Telemetry • Safe Memory", SvgIcons.ICON_SHIELD)
        );

        return statsBar;
    }

    private HBox createStatBox(String value, String label, String iconPath) {
        HBox box = new HBox(12);
        box.getStyleClass().add("stat-box");
        box.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(box, Priority.ALWAYS);

        box.getChildren().add(SvgIcons.createIcon(iconPath, 20, "#38BDF8"));

        VBox text = new VBox(2);
        Label valLabel = new Label(value);
        valLabel.getStyleClass().add("stat-number");
        Label lblLabel = new Label(label);
        lblLabel.getStyleClass().add("stat-label");

        text.getChildren().addAll(valLabel, lblLabel);
        box.getChildren().add(text);
        return box;
    }

    private GridPane createLabGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);

        // Lab 1: Classical
        LabCard classicalCard = new LabCard(
                "Classical Cryptography",
                "Explore historical substitution and transposition ciphers with interactive shift wheels and step-by-step trace tables.",
                SvgIcons.ICON_SHIELD,
                "#38BDF8",
                AlgorithmCategory.CLASSICAL,
                List.of("Caesar", "Atbash", "Vigenère", "Playfair", "Substitution"),
                () -> navigationManager.navigateTo(ViewId.CLASSICAL)
        );

        // Lab 2: Modern
        LabCard modernCard = new LabCard(
                "Modern Cryptography",
                "Examine block ciphers (AES-GCM, AES-CBC) with authenticated encryption, and educational public-key RSA with mathematical factor proofs.",
                SvgIcons.ICON_LOCK,
                "#818CF8",
                AlgorithmCategory.SYMMETRIC,
                List.of("AES-256-GCM", "AES-CBC", "RSA Math (p,q,n)", "Diffie-Hellman"),
                () -> navigationManager.navigateTo(ViewId.MODERN)
        );

        // Lab 3: Cryptanalysis
        LabCard analysisCard = new LabCard(
                "Cryptanalysis Lab",
                "Perform character frequency analysis, letter distribution histograms, Chi-Square scoring, and Caesar brute-force dictionary attacks.",
                SvgIcons.ICON_SEARCH,
                "#F59E0B",
                AlgorithmCategory.CRYPTANALYSIS,
                List.of("Frequency Histograms", "Caesar Brute Force", "Substitution Board"),
                () -> navigationManager.navigateTo(ViewId.CRYPTANALYSIS)
        );

        // Lab 4: Steganography
        LabCard stegoCard = new LabCard(
                "Steganography & Steganalysis",
                "Conceal secret messages and files in lossless PNG/BMP images using custom LSB channel bit-depths. Inspect changed pixels with Difference Maps.",
                SvgIcons.ICON_EYE,
                "#10B981",
                AlgorithmCategory.STEGANOGRAPHY,
                List.of("LSB PNG/BMP", "Bit-Level Visualizer", "Difference Map", "PSNR Metric"),
                () -> navigationManager.navigateTo(ViewId.STEGANOGRAPHY)
        );

        // Lab 5: Signatures
        LabCard signaturesCard = new LabCard(
                "Hashing & Digital Signatures",
                "Generate cryptographic digests (MD5, SHA-1, SHA-256, SHA-512) with avalanche effect visualization, and asymmetric digital signature tampering verification.",
                SvgIcons.ICON_PEN,
                "#EC4899",
                AlgorithmCategory.SIGNATURES,
                List.of("SHA Family", "Avalanche Effect", "Sign & Verify", "Tamper Test"),
                () -> navigationManager.navigateTo(ViewId.SIGNATURES)
        );

        // Lab 6: Learning & Exercises
        LabCard learningCard = new LabCard(
                "Exercises & Explorer",
                "Practical university-style student exercises, cryptanalysis challenges, algorithm comparative explorer, and local experiment history.",
                SvgIcons.ICON_BOOK,
                "#06B6D4",
                AlgorithmCategory.LEARNING,
                List.of("Student Exercises", "Concept Explorer", "Experiment History"),
                () -> navigationManager.navigateTo(ViewId.EXERCISES)
        );

        // Add to grid (2 columns)
        grid.add(classicalCard, 0, 0);
        grid.add(modernCard, 1, 0);
        grid.add(analysisCard, 0, 1);
        grid.add(stegoCard, 1, 1);
        grid.add(signaturesCard, 0, 2);
        grid.add(learningCard, 1, 2);

        GridPane.setHgrow(classicalCard, Priority.ALWAYS);
        GridPane.setHgrow(modernCard, Priority.ALWAYS);
        GridPane.setHgrow(analysisCard, Priority.ALWAYS);
        GridPane.setHgrow(stegoCard, Priority.ALWAYS);
        GridPane.setHgrow(signaturesCard, Priority.ALWAYS);
        GridPane.setHgrow(learningCard, Priority.ALWAYS);

        return grid;
    }
}
