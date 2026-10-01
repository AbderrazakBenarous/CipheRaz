package com.cryptolab.ui.views.signatures;

import com.cryptolab.app.AppContext;
import com.cryptolab.core.hashing.HashEngine;
import com.cryptolab.core.model.SecurityStatus;
import com.cryptolab.core.signatures.DigitalSignatureEngine;
import com.cryptolab.ui.components.SecurityNoticeBanner;
import com.cryptolab.ui.components.SvgIcons;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.security.KeyPair;
import java.util.Base64;

/**
 * Interactive Hashing and Digital Signatures Laboratory Workstation.
 * Features MD5/SHA digests, Avalanche Effect analyzer, and signature tampering simulation.
 *
 * Developed by Abderrazak BENAROUS
 */
public class SignaturesAndHashLabView extends ScrollPane {

    // Hash Controls
    private ComboBox<HashEngine.HashType> hashTypeCombo;
    private TextArea hashInputArea;
    private TextArea hashOutputArea;
    private Label hashInfoLabel;
    private VBox hashNoticeBox;
    private Label avalancheResultsLabel;
    private ProgressBar avalancheBar;

    // Signature Controls
    private ComboBox<DigitalSignatureEngine.SignatureScheme> sigSchemeCombo;
    private KeyPair currentKeyPair;
    private TextArea messageArea;
    private TextArea signatureArea;
    private Label verificationStatusLabel;
    private ListView<String> sigTraceList;

    public SignaturesAndHashLabView() {
        setFitToWidth(true);
        setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-padding: 0;");

        VBox root = new VBox(20);
        root.getStyleClass().add("content-area");

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Hashing & Digital Signatures Laboratory");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #F9FAFB;");
        header.getChildren().addAll(SvgIcons.createIcon(SvgIcons.ICON_PEN, 24, "#EC4899"), title);

        // Security Notice
        SecurityNoticeBanner banner = new SecurityNoticeBanner(
                SecurityStatus.SECURE_RECOMMENDED,
                "Hash functions provide integrity and collision resistance. MD5 and SHA-1 are deprecated due to practical collision attacks. Digital signatures combine hashing with asymmetric keys for non-repudiation."
        );

        // TabPane
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-tab-min-height: 32;");

        Tab hashTab = new Tab("Cryptographic Hashing & Avalanche Effect", createHashTabContent());
        hashTab.setClosable(false);

        Tab sigTab = new Tab("Digital Signatures & Tamper Detection", createSignatureTabContent());
        sigTab.setClosable(false);

        tabPane.getTabs().addAll(hashTab, sigTab);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        root.getChildren().addAll(header, banner, tabPane);
        setContent(root);
    }

    private VBox createHashTabContent() {
        VBox content = new VBox(14);
        content.setStyle("-fx-padding: 16 0 0 0;");

        HBox split = new HBox(20);

        // Left Col: Hash computation
        VBox leftCol = new VBox(12);
        leftCol.getStyleClass().add("lab-card");
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        Label title = new Label("HASH FUNCTION GENERATOR");
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #38BDF8;");

        HBox selRow = new HBox(12);
        selRow.setAlignment(Pos.CENTER_LEFT);
        hashTypeCombo = new ComboBox<>();
        hashTypeCombo.getItems().addAll(HashEngine.HashType.values());
        hashTypeCombo.setValue(HashEngine.HashType.SHA256);
        hashTypeCombo.setStyle("-fx-background-color: #1F2937; -fx-text-fill: #F9FAFB;");
        hashTypeCombo.valueProperty().addListener((obs, o, n) -> updateHashNotice(n));

        selRow.getChildren().addAll(new Label("Digest Algorithm:") {{ setStyle("-fx-text-fill: #9CA3AF;"); }}, hashTypeCombo);

        hashNoticeBox = new VBox(6);
        updateHashNotice(HashEngine.HashType.SHA256);

        hashInputArea = new TextArea("CRYPTOGRAPHY AND FORENSICS RESEARCH LABORATORY 2026");
        hashInputArea.setPromptText("Enter text to compute cryptographic hash digest...");
        hashInputArea.setWrapText(true);
        hashInputArea.setPrefRowCount(4);
        hashInputArea.setStyle("-fx-control-inner-background: #0B0F19; -fx-text-fill: #F9FAFB; -fx-font-family: monospace;");

        Button computeBtn = new Button("Compute Hash Digest");
        computeBtn.getStyleClass().add("btn-primary");
        computeBtn.setOnAction(e -> runHashCompute());

        hashOutputArea = new TextArea();
        hashOutputArea.setPromptText("Hex hash digest will appear here...");
        hashOutputArea.setWrapText(true);
        hashOutputArea.setEditable(false);
        hashOutputArea.setPrefRowCount(3);
        hashOutputArea.setStyle("-fx-control-inner-background: #0B0F19; -fx-text-fill: #10B981; -fx-font-family: monospace;");

        hashInfoLabel = new Label("Digest length: 256 bits (32 bytes)");
        hashInfoLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #9CA3AF;");

        leftCol.getChildren().addAll(title, selRow, hashNoticeBox, hashInputArea, computeBtn, hashOutputArea, hashInfoLabel);

        // Right Col: Avalanche Effect
        VBox rightCol = new VBox(12);
        rightCol.getStyleClass().add("lab-card");
        HBox.setHgrow(rightCol, Priority.ALWAYS);

        Label aTitle = new Label("AVALANCHE EFFECT DEMONSTRATOR");
        aTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #F59E0B;");

        Label aDesc = new Label(
                "A secure hash function exhibits the Avalanche Effect: changing a single bit in the input causes ~50% of the digest bits to flip unpredictably."
        );
        aDesc.setWrapText(true);
        aDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: #CBD5E1;");

        Button testAvalancheBtn = new Button("Simulate 1-Bit Input Flip");
        testAvalancheBtn.setStyle("-fx-background-color: #F59E0B; -fx-text-fill: #0B0F19; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
        testAvalancheBtn.setOnAction(e -> runAvalancheDemo());

        avalancheBar = new ProgressBar(0.0);
        avalancheBar.setMaxWidth(Double.MAX_VALUE);
        avalancheBar.setStyle("-fx-accent: #38BDF8;");

        avalancheResultsLabel = new Label("Click 'Simulate 1-Bit Input Flip' to evaluate bit diffusion.");
        avalancheResultsLabel.setStyle("-fx-font-family: monospace; -fx-font-size: 11px; -fx-text-fill: #9CA3AF; -fx-wrap-text: true;");

        rightCol.getChildren().addAll(aTitle, aDesc, testAvalancheBtn, avalancheBar, avalancheResultsLabel);

        split.getChildren().addAll(leftCol, rightCol);
        content.getChildren().add(split);

        // Run initial calculation
        computeBtn.fire();
        return content;
    }

    private void updateHashNotice(HashEngine.HashType type) {
        hashNoticeBox.getChildren().clear();
        if (type.getSecurityStatus() == SecurityStatus.DEPRECATED) {
            SecurityNoticeBanner warn = new SecurityNoticeBanner(type.getSecurityStatus(), type.getAdvisoryNote());
            hashNoticeBox.getChildren().add(warn);
        }
    }

    private void runHashCompute() {
        HashEngine.HashType type = hashTypeCombo.getValue();
        String text = hashInputArea.getText();
        String hex = HashEngine.hashToHex(text, type);
        hashOutputArea.setText(hex);
        hashInfoLabel.setText(String.format("Algorithm: %s • Digest Length: %d bits (%d bytes)",
                type.getAlgorithmName(), type.getDigestBits(), type.getDigestBits() / 8));
        AppContext.getInstance().setStatusMessage(type.getAlgorithmName() + " digest computed");
    }

    private void runAvalancheDemo() {
        HashEngine.HashType type = hashTypeCombo.getValue();
        String text = hashInputArea.getText();
        var av = HashEngine.evaluateAvalanche(text, type);

        avalancheBar.setProgress(av.percentageFlipped() / 100.0);
        avalancheResultsLabel.setText(String.format(
                "Original Text: '%s'\n" +
                "Modified Text: '%s' (1 bit flipped)\n\n" +
                "Original Digest: %s\n" +
                "Modified Digest: %s\n\n" +
                "Total Digest Bits: %d\n" +
                "Flipped Bits (Hamming Distance): %d\n" +
                "Diffusion Ratio: %.2f%% (Ideal target: ~50%%)",
                av.originalInput(), av.modifiedInput(),
                av.originalDigestHex(), av.modifiedDigestHex(),
                av.totalDigestBits(), av.flippedBits(), av.percentageFlipped()
        ));
        AppContext.getInstance().setStatusMessage(String.format("Avalanche effect evaluated: %.2f%% bits flipped", av.percentageFlipped()));
    }

    private VBox createSignatureTabContent() {
        VBox content = new VBox(14);
        content.setStyle("-fx-padding: 16 0 0 0;");

        HBox split = new HBox(20);

        // Left Col: Signing
        VBox leftCol = new VBox(12);
        leftCol.getStyleClass().add("lab-card");
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        Label title = new Label("DIGITAL SIGNATURE GENERATION");
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #EC4899;");

        HBox schemeRow = new HBox(12);
        schemeRow.setAlignment(Pos.CENTER_LEFT);
        sigSchemeCombo = new ComboBox<>();
        sigSchemeCombo.getItems().addAll(DigitalSignatureEngine.SignatureScheme.values());
        sigSchemeCombo.setValue(DigitalSignatureEngine.SignatureScheme.RSA_SHA256);
        sigSchemeCombo.setStyle("-fx-background-color: #1F2937; -fx-text-fill: #F9FAFB;");

        Button genKeyBtn = new Button("Generate Signer Key Pair");
        genKeyBtn.getStyleClass().add("btn-secondary");
        genKeyBtn.setOnAction(e -> {
            try {
                currentKeyPair = DigitalSignatureEngine.generateKeyPair(sigSchemeCombo.getValue());
                verificationStatusLabel.setText("KEY PAIR READY: Generated new " + sigSchemeCombo.getValue().name() + " keys.");
                verificationStatusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #38BDF8;");
                AppContext.getInstance().setStatusMessage("Generated signer key pair: " + sigSchemeCombo.getValue().name());
            } catch (Exception ex) {
                verificationStatusLabel.setText("Key generation error: " + ex.getMessage());
            }
        });

        schemeRow.getChildren().addAll(new Label("Signature Scheme:") {{ setStyle("-fx-text-fill: #9CA3AF;"); }}, sigSchemeCombo, genKeyBtn);

        messageArea = new TextArea("DISPATCH: AUTHORIZE WIRE TRANSFER OF $10,000 TO ACCOUNT #48920");
        messageArea.setPromptText("Enter message to sign...");
        messageArea.setWrapText(true);
        messageArea.setPrefRowCount(3);
        messageArea.setStyle("-fx-control-inner-background: #0B0F19; -fx-text-fill: #F9FAFB; -fx-font-family: monospace;");

        HBox actRow = new HBox(10);
        Button signBtn = new Button("Sign Message (Private Key)");
        signBtn.getStyleClass().add("btn-primary");
        signBtn.setOnAction(e -> runSigning());

        Button tamperBtn = new Button("Tamper Message ($10,000 -> $90,000)");
        tamperBtn.setStyle("-fx-background-color: rgba(239, 68, 68, 0.15); -fx-text-fill: #EF4444; -fx-border-color: #EF4444; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
        tamperBtn.setOnAction(e -> {
            messageArea.setText("DISPATCH: AUTHORIZE WIRE TRANSFER OF $90,000 TO ACCOUNT #48920");
            AppContext.getInstance().setStatusMessage("Message text modified (tampered)!");
        });

        actRow.getChildren().addAll(signBtn, tamperBtn);

        signatureArea = new TextArea();
        signatureArea.setPromptText("Generated digital signature (Base64)...");
        signatureArea.setWrapText(true);
        signatureArea.setEditable(false);
        signatureArea.setPrefRowCount(3);
        signatureArea.setStyle("-fx-control-inner-background: #0B0F19; -fx-text-fill: #EC4899; -fx-font-family: monospace; -fx-font-size: 11px;");

        leftCol.getChildren().addAll(title, schemeRow, messageArea, actRow, new Label("Digital Signature (Base64):") {{ setStyle("-fx-font-size: 11px; -fx-text-fill: #9CA3AF;"); }}, signatureArea);

        // Right Col: Verification & Tampering demonstration
        VBox rightCol = new VBox(12);
        rightCol.getStyleClass().add("lab-card");
        HBox.setHgrow(rightCol, Priority.ALWAYS);

        Label vTitle = new Label("SIGNATURE VERIFICATION & TAMPER PROOF");
        vTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #10B981;");

        Button verifyBtn = new Button("Verify Signature (Public Key)");
        verifyBtn.setStyle("-fx-background-color: #10B981; -fx-text-fill: #0B0F19; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
        verifyBtn.setOnAction(e -> runVerification());

        verificationStatusLabel = new Label("Click 'Verify Signature' to validate message integrity.");
        verificationStatusLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #9CA3AF; -fx-wrap-text: true;");

        sigTraceList = new ListView<>();
        sigTraceList.setStyle("-fx-control-inner-background: #0B0F19; -fx-text-fill: #9CA3AF; -fx-font-family: monospace; -fx-font-size: 11px;");
        VBox.setVgrow(sigTraceList, Priority.ALWAYS);

        rightCol.getChildren().addAll(vTitle, verifyBtn, verificationStatusLabel, new Label("Verification Protocol Steps:") {{ setStyle("-fx-font-size: 11px; -fx-text-fill: #9CA3AF;"); }}, sigTraceList);

        split.getChildren().addAll(leftCol, rightCol);
        content.getChildren().add(split);

        // Auto-generate keys on first load
        genKeyBtn.fire();
        return content;
    }

    private void runSigning() {
        if (currentKeyPair == null) {
            verificationStatusLabel.setText("Please generate a key pair first.");
            return;
        }
        try {
            var res = DigitalSignatureEngine.sign(messageArea.getText(), currentKeyPair.getPrivate(), sigSchemeCombo.getValue());
            signatureArea.setText(res.base64Signature());
            sigTraceList.getItems().setAll(res.traces());
            AppContext.getInstance().setStatusMessage("Message signed successfully using Private Key");
        } catch (Exception ex) {
            signatureArea.setText("Signing error: " + ex.getMessage());
        }
    }

    private void runVerification() {
        if (currentKeyPair == null) {
            verificationStatusLabel.setText("Please generate a key pair first.");
            return;
        }
        try {
            byte[] sigBytes = Base64.getDecoder().decode(signatureArea.getText().trim());
            var res = DigitalSignatureEngine.verify(messageArea.getText(), sigBytes, currentKeyPair.getPublic(), sigSchemeCombo.getValue());

            verificationStatusLabel.setText(res.statusMessage());
            verificationStatusLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + (res.valid() ? "#10B981" : "#EF4444") + ";");
            sigTraceList.getItems().setAll(res.traces());
            AppContext.getInstance().setStatusMessage(res.valid() ? "Signature verified successfully" : "Verification FAILED (tampered)");
        } catch (Exception ex) {
            verificationStatusLabel.setText("Verification error: " + ex.getMessage());
            verificationStatusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #EF4444;");
        }
    }
}
