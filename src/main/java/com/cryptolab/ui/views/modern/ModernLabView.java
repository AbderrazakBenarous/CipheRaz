package com.cryptolab.ui.views.modern;

import com.cryptolab.app.AppContext;
import com.cryptolab.core.model.CryptoResult;
import com.cryptolab.core.model.SecurityStatus;
import com.cryptolab.core.modern.AesEngine;
import com.cryptolab.core.modern.DiffieHellmanEngine;
import com.cryptolab.core.modern.RsaEngine;
import com.cryptolab.ui.components.SecurityNoticeBanner;
import com.cryptolab.ui.components.SvgIcons;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.math.BigInteger;
import java.security.KeyPair;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Interactive Modern Cryptography Workstation.
 * Covers AES (GCM/CBC), Educational & Production RSA, and Diffie-Hellman Key Exchange.
 *
 * Developed by Abderrazak BENAROUS
 */
public class ModernLabView extends ScrollPane {

    private final AesEngine aesEngine = new AesEngine();
    private final RsaEngine rsaEngine = new RsaEngine();
    private final DiffieHellmanEngine dhEngine = new DiffieHellmanEngine();

    // AES Controls
    private ComboBox<AesEngine.Mode> aesModeCombo;
    private ComboBox<Integer> aesKeySizeCombo;
    private TextField aesKeyField;
    private TextField aesIvField;
    private TextArea aesPlainInput;
    private TextArea aesOutputArea;
    private ListView<String> aesTraceList;

    // RSA Controls
    private RsaEngine.EducationalKeyParameters educationalParams;
    private KeyPair productionKeyPair;
    private Label rsaParamsLabel;
    private TextField rsaNumberInput;
    private TextArea rsaOutputArea;
    private ListView<String> rsaTraceList;
    private TextArea rsaPublicKeyArea;
    private TextArea rsaPrivateKeyArea;

    // Diffie-Hellman Controls
    private ListView<String> dhStepsList;
    private Label dhMatchLabel;

    public ModernLabView() {
        setFitToWidth(true);
        setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-padding: 0;");

        VBox root = new VBox(20);
        root.getStyleClass().add("content-area");

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Modern Cryptography Laboratory");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #F9FAFB;");
        header.getChildren().addAll(SvgIcons.createIcon(SvgIcons.ICON_LOCK, 24, "#818CF8"), title);

        // Security Notice
        SecurityNoticeBanner banner = new SecurityNoticeBanner(
                SecurityStatus.SECURE_RECOMMENDED,
                "Modern standards (AES-256-GCM, RSA-2048+) provide mathematical security proofs under current computing models. Nonce reuse in GCM completely destroys authentication."
        );

        // Tabs
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-tab-min-height: 32;");

        Tab aesTab = new Tab("Symmetric (AES-GCM / CBC)", createAesTabContent());
        aesTab.setClosable(false);

        Tab rsaTab = new Tab("Asymmetric (RSA Math & Production)", createRsaTabContent());
        rsaTab.setClosable(false);

        Tab dhTab = new Tab("Diffie-Hellman Key Exchange", createDhTabContent());
        dhTab.setClosable(false);

        tabPane.getTabs().addAll(aesTab, rsaTab, dhTab);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        root.getChildren().addAll(header, banner, tabPane);
        setContent(root);
    }

    private VBox createAesTabContent() {
        VBox content = new VBox(14);
        content.setStyle("-fx-padding: 16 0 0 0;");

        HBox split = new HBox(20);

        // Left Col: Parameters & Input
        VBox leftCol = new VBox(12);
        leftCol.getStyleClass().add("lab-card");
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        Label pTitle = new Label("AES CONFIGURATION & INPUT");
        pTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #38BDF8;");

        HBox cfgRow = new HBox(12);
        cfgRow.setAlignment(Pos.CENTER_LEFT);

        aesModeCombo = new ComboBox<>();
        aesModeCombo.getItems().addAll(AesEngine.Mode.values());
        aesModeCombo.setValue(AesEngine.Mode.GCM);
        aesModeCombo.setStyle("-fx-background-color: #1F2937; -fx-text-fill: #F9FAFB;");

        aesKeySizeCombo = new ComboBox<>();
        aesKeySizeCombo.getItems().addAll(128, 192, 256);
        aesKeySizeCombo.setValue(256);
        aesKeySizeCombo.setStyle("-fx-background-color: #1F2937; -fx-text-fill: #F9FAFB;");

        cfgRow.getChildren().addAll(new Label("Mode:") {{ setStyle("-fx-text-fill: #9CA3AF;"); }}, aesModeCombo,
                new Label("Key Bits:") {{ setStyle("-fx-text-fill: #9CA3AF;"); }}, aesKeySizeCombo);

        // Key Row
        HBox keyRow = new HBox(8);
        keyRow.setAlignment(Pos.CENTER_LEFT);
        aesKeyField = new TextField();
        aesKeyField.setPromptText("AES Key in Hex (32 bytes / 64 hex chars for AES-256)...");
        aesKeyField.setStyle("-fx-background-color: #0B0F19; -fx-text-fill: #F9FAFB; -fx-font-family: monospace;");
        HBox.setHgrow(aesKeyField, Priority.ALWAYS);

        Button genKeyBtn = new Button("Random Key");
        genKeyBtn.getStyleClass().add("btn-secondary");
        genKeyBtn.setOnAction(e -> {
            byte[] k = AesEngine.generateRandomKey(aesKeySizeCombo.getValue());
            aesKeyField.setText(HexFormat.of().formatHex(k));
        });
        keyRow.getChildren().addAll(aesKeyField, genKeyBtn);

        // IV / Nonce Row
        HBox ivRow = new HBox(8);
        ivRow.setAlignment(Pos.CENTER_LEFT);
        aesIvField = new TextField();
        aesIvField.setPromptText("IV/Nonce in Hex (12 bytes for GCM, 16 for CBC)...");
        aesIvField.setStyle("-fx-background-color: #0B0F19; -fx-text-fill: #F9FAFB; -fx-font-family: monospace;");
        HBox.setHgrow(aesIvField, Priority.ALWAYS);

        Button genIvBtn = new Button("Random IV");
        genIvBtn.getStyleClass().add("btn-secondary");
        genIvBtn.setOnAction(e -> {
            byte[] iv = AesEngine.generateRandomIv(aesModeCombo.getValue().getIvLengthBytes());
            aesIvField.setText(HexFormat.of().formatHex(iv));
        });
        ivRow.getChildren().addAll(aesIvField, genIvBtn);

        // Populate initial key and IV
        genKeyBtn.fire();
        genIvBtn.fire();

        aesPlainInput = new TextArea("CONFIDENTIAL OPERATIONAL DISPATCH: SECURITY CLEARANCE LEVEL 5");
        aesPlainInput.setPromptText("Enter plaintext message to encrypt...");
        aesPlainInput.setWrapText(true);
        aesPlainInput.setPrefRowCount(4);
        aesPlainInput.setStyle("-fx-control-inner-background: #0B0F19; -fx-text-fill: #F9FAFB; -fx-font-family: monospace;");

        HBox actionBox = new HBox(12);
        Button encBtn = new Button("Encrypt (AES)");
        encBtn.getStyleClass().add("btn-primary");
        encBtn.setOnAction(e -> runAesEncrypt());

        Button decBtn = new Button("Decrypt (AES)");
        decBtn.getStyleClass().add("btn-secondary");
        decBtn.setOnAction(e -> runAesDecrypt());

        actionBox.getChildren().addAll(encBtn, decBtn);
        leftCol.getChildren().addAll(pTitle, cfgRow, new Label("Secret Key:") {{ setStyle("-fx-font-size: 11px; -fx-text-fill: #9CA3AF;"); }}, keyRow,
                new Label("Initialization Vector (Nonce):") {{ setStyle("-fx-font-size: 11px; -fx-text-fill: #9CA3AF;"); }}, ivRow, aesPlainInput, actionBox);

        // Right Col: Output & Block Tracer
        VBox rightCol = new VBox(12);
        rightCol.getStyleClass().add("lab-card");
        HBox.setHgrow(rightCol, Priority.ALWAYS);

        Label oTitle = new Label("CIPHERTEXT & AEAD VERIFICATION TRACE");
        oTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #818CF8;");

        aesOutputArea = new TextArea();
        aesOutputArea.setPromptText("Encrypted / Decrypted output will appear here...");
        aesOutputArea.setWrapText(true);
        aesOutputArea.setEditable(false);
        aesOutputArea.setPrefRowCount(5);
        aesOutputArea.setStyle("-fx-control-inner-background: #0B0F19; -fx-text-fill: #38BDF8; -fx-font-family: monospace;");

        aesTraceList = new ListView<>();
        aesTraceList.setStyle("-fx-control-inner-background: #0B0F19; -fx-text-fill: #9CA3AF; -fx-font-family: monospace; -fx-font-size: 11px;");
        VBox.setVgrow(aesTraceList, Priority.ALWAYS);

        rightCol.getChildren().addAll(oTitle, aesOutputArea, new Label("Cryptographic State Trace:") {{ setStyle("-fx-font-size: 11px; -fx-text-fill: #9CA3AF;"); }}, aesTraceList);

        split.getChildren().addAll(leftCol, rightCol);
        content.getChildren().add(split);
        return content;
    }

    private void runAesEncrypt() {
        try {
            byte[] key = HexFormat.of().parseHex(aesKeyField.getText().trim());
            byte[] iv = HexFormat.of().parseHex(aesIvField.getText().trim());
            CryptoResult res = aesEngine.encrypt(aesPlainInput.getText(), key, aesModeCombo.getValue(), iv);
            if (res.success()) {
                aesOutputArea.setText(res.outputText());
                aesTraceList.getItems().setAll(res.stepTraces());
                AppContext.getInstance().setStatusMessage("AES encryption complete");
            } else {
                aesOutputArea.setText("ERROR: " + res.errorMessage());
                aesTraceList.getItems().clear();
                AppContext.getInstance().setStatusMessage("AES Error: " + res.errorMessage());
            }
        } catch (Exception ex) {
            aesOutputArea.setText("Format Error: Ensure key and IV are valid hex strings: " + ex.getMessage());
        }
    }

    private void runAesDecrypt() {
        try {
            byte[] key = HexFormat.of().parseHex(aesKeyField.getText().trim());
            byte[] iv = HexFormat.of().parseHex(aesIvField.getText().trim());
            // Extract raw ciphertext hex from output if present
            String raw = aesOutputArea.getText();
            String cipherHex = raw;
            if (raw.contains("Ciphertext (Hex): ")) {
                int start = raw.indexOf("Ciphertext (Hex): ") + "Ciphertext (Hex): ".length();
                int end = raw.indexOf("\n", start);
                cipherHex = (end != -1) ? raw.substring(start, end).trim() : raw.substring(start).trim();
            }
            byte[] cipherBytes = HexFormat.of().parseHex(cipherHex);
            CryptoResult res = aesEngine.decrypt(cipherBytes, key, aesModeCombo.getValue(), iv);
            if (res.success()) {
                aesOutputArea.setText("Decrypted Plaintext:\n" + res.outputText());
                aesTraceList.getItems().setAll(res.stepTraces());
                AppContext.getInstance().setStatusMessage("AES decryption verified successfully");
            } else {
                aesOutputArea.setText("DECRYPTION ERROR:\n" + res.errorMessage());
                aesTraceList.getItems().clear();
                AppContext.getInstance().setStatusMessage("Decryption failed: " + res.errorMessage());
            }
        } catch (Exception ex) {
            aesOutputArea.setText("Decryption input error: " + ex.getMessage());
        }
    }

    private VBox createRsaTabContent() {
        VBox content = new VBox(14);
        content.setStyle("-fx-padding: 16 0 0 0;");

        HBox split = new HBox(20);

        // Left Col: Educational Math Mode
        VBox mathCard = new VBox(12);
        mathCard.getStyleClass().add("lab-card");
        HBox.setHgrow(mathCard, Priority.ALWAYS);

        Label mTitle = new Label("1. EDUCATIONAL RSA MATH ENGINE (TOY PARAMETERS)");
        mTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #818CF8;");

        Button genToyBtn = new Button("Generate Toy Primes (16-bit)");
        genToyBtn.getStyleClass().add("btn-secondary");
        genToyBtn.setOnAction(e -> {
            educationalParams = RsaEngine.generateEducationalKeys(16);
            rsaParamsLabel.setText(String.format(
                    "p = %s\nq = %s\nModulus n = p*q = %s\nTotient phi(n) = %s\nPublic e = %s\nPrivate d = %s",
                    educationalParams.p(), educationalParams.q(), educationalParams.n(),
                    educationalParams.phi(), educationalParams.e(), educationalParams.d()
            ));
        });

        rsaParamsLabel = new Label("Click 'Generate Toy Primes' to calculate p, q, n, phi, e, and d.");
        rsaParamsLabel.setStyle("-fx-font-family: monospace; -fx-font-size: 12px; -fx-text-fill: #38BDF8;");

        rsaNumberInput = new TextField("42");
        rsaNumberInput.setPromptText("Enter numerical message integer m < n...");
        rsaNumberInput.setStyle("-fx-background-color: #0B0F19; -fx-text-fill: #F9FAFB; -fx-font-family: monospace;");

        HBox mathActionRow = new HBox(10);
        Button encMathBtn = new Button("Encrypt: c = m^e mod n");
        encMathBtn.getStyleClass().add("btn-primary");
        encMathBtn.setOnAction(e -> {
            if (educationalParams == null) genToyBtn.fire();
            try {
                BigInteger m = new BigInteger(rsaNumberInput.getText().trim());
                CryptoResult res = rsaEngine.encryptEducational(m, educationalParams);
                if (res.success()) {
                    rsaOutputArea.setText("Ciphertext c = " + res.outputText());
                    rsaTraceList.getItems().setAll(res.stepTraces());
                } else {
                    rsaOutputArea.setText("Error: " + res.errorMessage());
                }
            } catch (Exception ex) {
                rsaOutputArea.setText("Invalid integer: " + ex.getMessage());
            }
        });

        Button decMathBtn = new Button("Decrypt: m = c^d mod n");
        decMathBtn.getStyleClass().add("btn-secondary");
        decMathBtn.setOnAction(e -> {
            if (educationalParams == null) return;
            try {
                String cStr = rsaOutputArea.getText().replace("Ciphertext c = ", "").trim();
                BigInteger c = new BigInteger(cStr);
                CryptoResult res = rsaEngine.decryptEducational(c, educationalParams);
                if (res.success()) {
                    rsaOutputArea.setText("Decrypted m = " + res.outputText());
                    rsaTraceList.getItems().setAll(res.stepTraces());
                }
            } catch (Exception ex) {
                rsaOutputArea.setText("Decryption error: " + ex.getMessage());
            }
        });

        mathActionRow.getChildren().addAll(encMathBtn, decMathBtn);
        mathCard.getChildren().addAll(mTitle, genToyBtn, rsaParamsLabel, new Label("Plaintext Integer (m < n):") {{ setStyle("-fx-font-size: 11px; -fx-text-fill: #9CA3AF;"); }}, rsaNumberInput, mathActionRow);

        // Right Col: Production RSA Mode
        VBox prodCard = new VBox(12);
        prodCard.getStyleClass().add("lab-card");
        HBox.setHgrow(prodCard, Priority.ALWAYS);

        Label pTitle = new Label("2. PRODUCTION RSA (2048-BIT OAEP)");
        pTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #10B981;");

        Button genProdKeyBtn = new Button("Generate 2048-bit RSA Key Pair");
        genProdKeyBtn.getStyleClass().add("btn-secondary");
        genProdKeyBtn.setOnAction(e -> {
            try {
                productionKeyPair = RsaEngine.generateProductionKeyPair(2048);
                rsaPublicKeyArea.setText(Base64.getEncoder().encodeToString(productionKeyPair.getPublic().getEncoded()));
                rsaPrivateKeyArea.setText("PRIVATE KEY SECURELY STORED IN MEMORY (" + productionKeyPair.getPrivate().getFormat() + " / 2048 bits)");
                AppContext.getInstance().setStatusMessage("Generated fresh 2048-bit RSA key pair");
            } catch (Exception ex) {
                rsaPublicKeyArea.setText("Key generation error: " + ex.getMessage());
            }
        });

        rsaPublicKeyArea = new TextArea();
        rsaPublicKeyArea.setPromptText("Public Key (Base64 X.509 format)...");
        rsaPublicKeyArea.setWrapText(true);
        rsaPublicKeyArea.setPrefRowCount(3);
        rsaPublicKeyArea.setStyle("-fx-control-inner-background: #0B0F19; -fx-text-fill: #9CA3AF; -fx-font-size: 10px; -fx-font-family: monospace;");

        rsaPrivateKeyArea = new TextArea("Click 'Generate 2048-bit RSA Key Pair' above.");
        rsaPrivateKeyArea.setWrapText(true);
        rsaPrivateKeyArea.setPrefRowCount(2);
        rsaPrivateKeyArea.setEditable(false);
        rsaPrivateKeyArea.setStyle("-fx-control-inner-background: #0B0F19; -fx-text-fill: #F59E0B; -fx-font-size: 11px;");

        rsaOutputArea = new TextArea();
        rsaOutputArea.setPromptText("RSA calculation output...");
        rsaOutputArea.setPrefRowCount(3);
        rsaOutputArea.setStyle("-fx-control-inner-background: #0B0F19; -fx-text-fill: #38BDF8; -fx-font-family: monospace;");

        rsaTraceList = new ListView<>();
        rsaTraceList.setStyle("-fx-control-inner-background: #0B0F19; -fx-text-fill: #9CA3AF; -fx-font-family: monospace; -fx-font-size: 11px;");
        VBox.setVgrow(rsaTraceList, Priority.ALWAYS);

        prodCard.getChildren().addAll(pTitle, genProdKeyBtn, new Label("Public Key:") {{ setStyle("-fx-font-size: 11px; -fx-text-fill: #9CA3AF;"); }}, rsaPublicKeyArea,
                new Label("Private Key Status:") {{ setStyle("-fx-font-size: 11px; -fx-text-fill: #9CA3AF;"); }}, rsaPrivateKeyArea, rsaOutputArea, rsaTraceList);

        split.getChildren().addAll(mathCard, prodCard);
        content.getChildren().add(split);
        return content;
    }

    private VBox createDhTabContent() {
        VBox content = new VBox(14);
        content.getStyleClass().add("lab-card");

        Label title = new Label("DIFFIE-HELLMAN KEY EXCHANGE SIMULATOR");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #38BDF8;");

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Button runDhBtn = new Button("Simulate Key Exchange (24-bit primes)");
        runDhBtn.getStyleClass().add("btn-primary");
        runDhBtn.setOnAction(e -> {
            var result = dhEngine.simulateExchange(24);
            dhStepsList.getItems().setAll(result.protocolSteps());
            dhMatchLabel.setText(result.secretsMatch()
                    ? "SUCCESS: Both parties derived identical shared secret = " + result.sharedSecretAlice()
                    : "MISMATCH: Protocol failure");
            dhMatchLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + (result.secretsMatch() ? "#10B981" : "#EF4444") + ";");
        });

        dhMatchLabel = new Label("Click 'Simulate Key Exchange' to run the protocol.");
        dhMatchLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #9CA3AF;");

        topRow.getChildren().addAll(runDhBtn, dhMatchLabel);

        dhStepsList = new ListView<>();
        dhStepsList.setStyle("-fx-control-inner-background: #0B0F19; -fx-text-fill: #CBD5E1; -fx-font-family: monospace; -fx-font-size: 12px;");
        VBox.setVgrow(dhStepsList, Priority.ALWAYS);

        content.getChildren().addAll(title, topRow, dhStepsList);
        return content;
    }
}
