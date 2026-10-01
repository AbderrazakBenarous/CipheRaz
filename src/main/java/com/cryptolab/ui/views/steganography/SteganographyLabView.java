package com.cryptolab.ui.views.steganography;

import com.cryptolab.app.AppContext;
import com.cryptolab.core.model.SecurityStatus;
import com.cryptolab.core.steganalysis.SteganalysisEngine;
import com.cryptolab.core.steganography.LsbStegoEngine;
import com.cryptolab.core.steganography.StegoContainer;
import com.cryptolab.ui.components.SecurityNoticeBanner;
import com.cryptolab.ui.components.SvgIcons;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * Interactive Steganography and Steganalysis Laboratory Workstation.
 * Features LSB embedding (1-4 bpc), bit-level pixel inspectors, difference maps,
 * MSE/PSNR calculation, optional AES-GCM password encryption, and bit-plane visualizer.
 *
 * Developed by Abderrazak BENAROUS
 */
public class SteganographyLabView extends ScrollPane {

    private final LsbStegoEngine stegoEngine = new LsbStegoEngine();

    // In-memory images
    private BufferedImage coverImage;
    private BufferedImage currentStegoImage;
    private BufferedImage currentDiffMap;

    // UI - Embed
    private ImageView coverImageView;
    private ImageView stegoImageView;
    private ImageView diffMapImageView;
    private TextArea embedPayloadArea;
    private PasswordField embedPasswordField;
    private ComboBox<Integer> bpcCombo;
    private Label capacityLabel;
    private Label metricsLabel;
    private ListView<String> bitTraceList;

    // UI - Extract
    private ImageView extractPreviewView;
    private PasswordField extractPasswordField;
    private TextArea extractedOutputArea;
    private Label extractStatusLabel;

    // UI - Steganalysis
    private ImageView bitPlaneView;
    private ComboBox<SteganalysisEngine.Channel> stegChannelCombo;
    private ComboBox<Integer> bitPlaneCombo;
    private Label anomalyLabel;

    public SteganographyLabView() {
        setFitToWidth(true);
        setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-padding: 0;");

        VBox root = new VBox(20);
        root.getStyleClass().add("content-area");

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Steganography & Steganalysis Laboratory");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #F9FAFB;");
        header.getChildren().addAll(SvgIcons.createIcon(SvgIcons.ICON_EYE, 24, "#10B981"), title);

        // Security Notice
        SecurityNoticeBanner banner = new SecurityNoticeBanner(
                SecurityStatus.SECURE_RECOMMENDED,
                "Steganography conceals the existence of communication, while encryption protects its content. Combining AES-GCM with LSB embedding provides both confidentiality and deniability."
        );

        // Create default test image
        coverImage = createTestPattern(320, 240);

        // TabPane
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-tab-min-height: 32;");

        Tab embedTab = new Tab("1. LSB Embedding & Difference Map", createEmbedTabContent());
        embedTab.setClosable(false);

        Tab extractTab = new Tab("2. Payload Detection & Extraction", createExtractTabContent());
        extractTab.setClosable(false);

        Tab stegTab = new Tab("3. Steganalysis & Bit-Plane Inspector", createSteganalysisTabContent());
        stegTab.setClosable(false);

        tabPane.getTabs().addAll(embedTab, extractTab, stegTab);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        root.getChildren().addAll(header, banner, tabPane);
        setContent(root);

        // Initial capacity update
        updateCapacityDisplay();
    }

    private VBox createEmbedTabContent() {
        VBox content = new VBox(14);
        content.setStyle("-fx-padding: 16 0 0 0;");

        HBox mainSplit = new HBox(20);

        // Left Col: Controls & Input
        VBox leftCol = new VBox(12);
        leftCol.getStyleClass().add("lab-card");
        leftCol.setPrefWidth(420);

        Label title = new Label("EMBEDDING CONFIGURATION");
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #10B981;");

        HBox imgBtnRow = new HBox(8);
        Button sampleImgBtn = new Button("Load Test Pattern (320x240)");
        sampleImgBtn.getStyleClass().add("btn-secondary");
        sampleImgBtn.setOnAction(e -> {
            coverImage = createTestPattern(320, 240);
            coverImageView.setImage(SwingFXUtils.toFXImage(coverImage, null));
            updateCapacityDisplay();
        });

        Button browseImgBtn = new Button("Browse PNG/BMP...");
        browseImgBtn.getStyleClass().add("btn-secondary");
        browseImgBtn.setOnAction(e -> browseCoverImage());

        imgBtnRow.getChildren().addAll(sampleImgBtn, browseImgBtn);

        // BPC Selector
        HBox bpcRow = new HBox(10);
        bpcRow.setAlignment(Pos.CENTER_LEFT);
        bpcCombo = new ComboBox<>();
        bpcCombo.getItems().addAll(1, 2, 3, 4);
        bpcCombo.setValue(1);
        bpcCombo.setStyle("-fx-background-color: #1F2937; -fx-text-fill: #F9FAFB;");
        bpcCombo.valueProperty().addListener((obs, o, n) -> updateCapacityDisplay());

        capacityLabel = new Label("Capacity: --");
        capacityLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #38BDF8;");

        bpcRow.getChildren().addAll(new Label("Bits/Channel:") {{ setStyle("-fx-text-fill: #9CA3AF;"); }}, bpcCombo, capacityLabel);

        // Payload Input
        embedPayloadArea = new TextArea("SECRET DISPATCH: Operation Antigravity initiated. Coordinates: 48.8584 N, 2.2945 E.");
        embedPayloadArea.setPromptText("Enter secret text message to conceal...");
        embedPayloadArea.setWrapText(true);
        embedPayloadArea.setPrefRowCount(3);
        embedPayloadArea.setStyle("-fx-control-inner-background: #0B0F19; -fx-text-fill: #F9FAFB; -fx-font-family: monospace;");

        // Optional AES Encryption
        HBox pwdRow = new HBox(8);
        pwdRow.setAlignment(Pos.CENTER_LEFT);
        embedPasswordField = new PasswordField();
        embedPasswordField.setPromptText("Optional Passphrase for AES-256-GCM...");
        embedPasswordField.setStyle("-fx-background-color: #0B0F19; -fx-text-fill: #F9FAFB;");
        HBox.setHgrow(embedPasswordField, Priority.ALWAYS);
        pwdRow.getChildren().addAll(new Label("Encryption:") {{ setStyle("-fx-text-fill: #9CA3AF;"); }}, embedPasswordField);

        // Embed Button
        Button embedBtn = new Button("Conceal Payload in Image (LSB)");
        embedBtn.getStyleClass().add("btn-primary");
        embedBtn.setOnAction(e -> runEmbedding());

        Button saveStegoBtn = new Button("Save Stego Image (PNG)");
        saveStegoBtn.getStyleClass().add("btn-secondary");
        saveStegoBtn.setOnAction(e -> saveStegoImage());

        leftCol.getChildren().addAll(title, imgBtnRow, bpcRow, new Label("Secret Message:") {{ setStyle("-fx-font-size: 11px; -fx-text-fill: #9CA3AF;"); }},
                embedPayloadArea, pwdRow, embedBtn, saveStegoBtn);

        // Right Col: Visualizations & Metrics
        VBox rightCol = new VBox(12);
        rightCol.getStyleClass().add("lab-card");
        HBox.setHgrow(rightCol, Priority.ALWAYS);

        Label vTitle = new Label("IMAGE FIDELITY & DIFFERENCE MAP");
        vTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #38BDF8;");

        HBox imgDisplays = new HBox(16);
        imgDisplays.setAlignment(Pos.CENTER);

        VBox box1 = createImgPreviewBox("Cover Image", coverImageView = new ImageView());
        VBox box2 = createImgPreviewBox("Stego Image", stegoImageView = new ImageView());
        VBox box3 = createImgPreviewBox("Difference Map (50x Amp)", diffMapImageView = new ImageView());

        coverImageView.setImage(SwingFXUtils.toFXImage(coverImage, null));
        imgDisplays.getChildren().addAll(box1, box2, box3);

        metricsLabel = new Label("Run embedding to calculate MSE, PSNR, and pixel modification statistics.");
        metricsLabel.setStyle("-fx-font-family: monospace; -fx-font-size: 11px; -fx-text-fill: #CBD5E1; -fx-wrap-text: true;");

        bitTraceList = new ListView<>();
        bitTraceList.setPrefHeight(150);
        bitTraceList.setStyle("-fx-control-inner-background: #0B0F19; -fx-text-fill: #38BDF8; -fx-font-family: monospace; -fx-font-size: 10px;");

        rightCol.getChildren().addAll(vTitle, imgDisplays, metricsLabel, new Label("Bit-Level Pixel Inspection (First modified pixels):") {{
            setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #9CA3AF;");
        }}, bitTraceList);

        mainSplit.getChildren().addAll(leftCol, rightCol);
        content.getChildren().add(mainSplit);

        // Run default embed on startup
        embedBtn.fire();
        return content;
    }

    private VBox createImgPreviewBox(String label, ImageView view) {
        VBox box = new VBox(6);
        box.setAlignment(Pos.CENTER);
        Label l = new Label(label);
        l.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #9CA3AF;");
        view.setFitWidth(180);
        view.setFitHeight(135);
        view.setPreserveRatio(true);
        view.setStyle("-fx-border-color: #374151; -fx-border-width: 1;");
        box.getChildren().addAll(l, view);
        return box;
    }

    private void updateCapacityDisplay() {
        if (coverImage == null) return;
        int bpc = bpcCombo.getValue() != null ? bpcCombo.getValue() : 1;
        int bytes = LsbStegoEngine.calculateCapacityBytes(coverImage.getWidth(), coverImage.getHeight(), bpc, false);
        capacityLabel.setText(String.format("Capacity: %,d bytes (%.1f KB)", bytes, bytes / 1024.0));
    }

    private void runEmbedding() {
        try {
            int bpc = bpcCombo.getValue();
            String message = embedPayloadArea.getText();
            String pwd = embedPasswordField.getText();

            byte[] container = StegoContainer.pack(
                    message.getBytes(StandardCharsets.UTF_8),
                    StegoContainer.TYPE_TEXT,
                    (byte) bpc,
                    pwd
            );

            LsbStegoEngine.StegoResult res = stegoEngine.embed(coverImage, container, bpc, false);
            currentStegoImage = res.stegoImage();
            currentDiffMap = res.differenceMap();

            stegoImageView.setImage(SwingFXUtils.toFXImage(currentStegoImage, null));
            diffMapImageView.setImage(SwingFXUtils.toFXImage(currentDiffMap, null));

            metricsLabel.setText(String.format(
                    "Payload: %d bytes (%.1f%% of capacity used)\n" +
                    "Pixels Modified: %d of %d (%.2f%%)\n" +
                    "MSE (Mean Squared Error): %.5f\n" +
                    "PSNR (Peak Signal-to-Noise Ratio): %.2f dB (Quality: Excellent)",
                    res.payloadBytes(), res.capacityUsedPercentage(),
                    res.changedPixelsCount(), coverImage.getWidth() * coverImage.getHeight(), res.changedPixelsPercentage(),
                    res.mse(), res.psnrDb()
            ));

            bitTraceList.getItems().setAll(res.bitTraces());
            AppContext.getInstance().setStatusMessage(String.format("Embedded %d bytes at %d bpc (PSNR: %.2f dB)", res.payloadBytes(), bpc, res.psnrDb()));
        } catch (Exception ex) {
            metricsLabel.setText("Embedding Error: " + ex.getMessage());
            AppContext.getInstance().setStatusMessage("Steganography Error: " + ex.getMessage());
        }
    }

    private VBox createExtractTabContent() {
        VBox content = new VBox(14);
        content.getStyleClass().add("lab-card");

        Label title = new Label("EXTRACT HIDDEN PAYLOAD FROM STEGO IMAGE");
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #38BDF8;");

        HBox btnRow = new HBox(12);
        btnRow.setAlignment(Pos.CENTER_LEFT);

        Button useCurrentBtn = new Button("Use Current Stego Image");
        useCurrentBtn.getStyleClass().add("btn-secondary");
        useCurrentBtn.setOnAction(e -> {
            if (currentStegoImage != null) {
                extractPreviewView.setImage(SwingFXUtils.toFXImage(currentStegoImage, null));
                extractStatusLabel.setText("Loaded current active stego image.");
            }
        });

        Button browseBtn = new Button("Open Stego File...");
        browseBtn.getStyleClass().add("btn-secondary");
        browseBtn.setOnAction(e -> browseStegoImageForExtract());

        extractPasswordField = new PasswordField();
        extractPasswordField.setPromptText("Decryption Passphrase (if encrypted)...");
        extractPasswordField.setStyle("-fx-background-color: #0B0F19; -fx-text-fill: #F9FAFB;");
        HBox.setHgrow(extractPasswordField, Priority.ALWAYS);

        Button extractBtn = new Button("Detect & Extract Payload");
        extractBtn.getStyleClass().add("btn-primary");
        extractBtn.setOnAction(e -> runExtraction());

        btnRow.getChildren().addAll(useCurrentBtn, browseBtn, extractPasswordField, extractBtn);

        HBox displayRow = new HBox(16);
        extractPreviewView = new ImageView();
        extractPreviewView.setFitWidth(180);
        extractPreviewView.setFitHeight(135);
        extractPreviewView.setPreserveRatio(true);
        if (currentStegoImage != null) {
            extractPreviewView.setImage(SwingFXUtils.toFXImage(currentStegoImage, null));
        }

        VBox rightBox = new VBox(8);
        HBox.setHgrow(rightBox, Priority.ALWAYS);

        extractStatusLabel = new Label("Ready to extract. If payload was password-encrypted, enter the key above.");
        extractStatusLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #38BDF8;");

        extractedOutputArea = new TextArea();
        extractedOutputArea.setPromptText("Recovered secret payload text will be displayed here...");
        extractedOutputArea.setWrapText(true);
        extractedOutputArea.setPrefRowCount(5);
        extractedOutputArea.setStyle("-fx-control-inner-background: #0B0F19; -fx-text-fill: #10B981; -fx-font-family: monospace;");

        rightBox.getChildren().addAll(extractStatusLabel, extractedOutputArea);
        displayRow.getChildren().addAll(extractPreviewView, rightBox);

        content.getChildren().addAll(title, btnRow, displayRow);
        return content;
    }

    private void runExtraction() {
        BufferedImage target = (currentStegoImage != null) ? currentStegoImage : coverImage;
        if (target == null) {
            extractStatusLabel.setText("No stego image loaded.");
            return;
        }

        try {
            int bpc = bpcCombo.getValue();
            String pwd = extractPasswordField.getText();

            // Extract maximum potential container bytes from image
            byte[] rawExtracted = stegoEngine.extract(target, bpc, false, 0);

            // Unpack CLAB container
            StegoContainer.ContainerPayload payload = StegoContainer.unpack(rawExtracted, pwd);

            extractStatusLabel.setText(String.format("SUCCESS: CLAB Container Verified! (Type: %s, Encrypted: %b, Size: %d bytes)",
                    payload.payloadType() == StegoContainer.TYPE_TEXT ? "UTF-8 Text" : "Binary",
                    payload.encrypted(), payload.rawData().length));
            extractStatusLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #10B981;");

            extractedOutputArea.setText(payload.textContent());
            AppContext.getInstance().setStatusMessage("Payload successfully extracted and verified");
        } catch (Exception ex) {
            extractStatusLabel.setText("Extraction Failed: " + ex.getMessage());
            extractStatusLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #EF4444;");
            extractedOutputArea.clear();
            AppContext.getInstance().setStatusMessage("Extraction Error: " + ex.getMessage());
        }
    }

    private VBox createSteganalysisTabContent() {
        VBox content = new VBox(14);
        content.getStyleClass().add("lab-card");

        Label title = new Label("STEGANALYSIS & BIT-PLANE FILTER");
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #F59E0B;");

        HBox ctrlRow = new HBox(12);
        ctrlRow.setAlignment(Pos.CENTER_LEFT);

        stegChannelCombo = new ComboBox<>();
        stegChannelCombo.getItems().addAll(SteganalysisEngine.Channel.values());
        stegChannelCombo.setValue(SteganalysisEngine.Channel.RED);
        stegChannelCombo.setStyle("-fx-background-color: #1F2937; -fx-text-fill: #F9FAFB;");

        bitPlaneCombo = new ComboBox<>();
        bitPlaneCombo.getItems().addAll(0, 1, 2, 3, 4, 5, 6, 7);
        bitPlaneCombo.setValue(0); // LSB
        bitPlaneCombo.setStyle("-fx-background-color: #1F2937; -fx-text-fill: #F9FAFB;");

        Button filterBtn = new Button("Inspect Bit Plane");
        filterBtn.getStyleClass().add("btn-primary");
        filterBtn.setOnAction(e -> runSteganalysisFilter());

        ctrlRow.getChildren().addAll(new Label("Channel:") {{ setStyle("-fx-text-fill: #9CA3AF;"); }}, stegChannelCombo,
                new Label("Bit Plane (0=LSB):") {{ setStyle("-fx-text-fill: #9CA3AF;"); }}, bitPlaneCombo, filterBtn);

        HBox displayRow = new HBox(20);
        bitPlaneView = new ImageView();
        bitPlaneView.setFitWidth(320);
        bitPlaneView.setFitHeight(240);
        bitPlaneView.setPreserveRatio(true);
        bitPlaneView.setStyle("-fx-border-color: #374151; -fx-border-width: 1;");

        VBox rightSide = new VBox(10);
        HBox.setHgrow(rightSide, Priority.ALWAYS);

        anomalyLabel = new Label("Run filter to compute parity anomaly indicators.");
        anomalyLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #CBD5E1; -fx-wrap-text: true;");

        Label notes = new Label(
                "Educational Context:\n" +
                "• Bit Plane 0 (LSB) reveals low-level noise. In uncompressed images, natural photos exhibit continuous random noise.\n" +
                "• Non-random regular streaks or sharp boundary transitions in Bit Plane 0 often point to unencrypted data embedding.\n" +
                "• Encrypted payloads appear indistinguishable from true random noise, illustrating why modern steganography pairs with encryption."
        );
        notes.setWrapText(true);
        notes.setStyle("-fx-font-size: 11px; -fx-text-fill: #94A3B8;");

        rightSide.getChildren().addAll(anomalyLabel, notes);
        displayRow.getChildren().addAll(bitPlaneView, rightSide);

        content.getChildren().addAll(title, ctrlRow, displayRow);

        // Run default filter
        filterBtn.fire();
        return content;
    }

    private void runSteganalysisFilter() {
        BufferedImage target = (currentStegoImage != null) ? currentStegoImage : coverImage;
        if (target == null) return;

        SteganalysisEngine.Channel ch = stegChannelCombo.getValue();
        int bit = bitPlaneCombo.getValue();

        BufferedImage plane = SteganalysisEngine.extractBitPlane(target, ch, bit);
        bitPlaneView.setImage(SwingFXUtils.toFXImage(plane, null));

        var anomaly = SteganalysisEngine.evaluateAnomaly(target);
        anomalyLabel.setText(String.format("Anomaly Metric: %.1f%% • %s\nEvaluated Pixels: %,d • LSB Zero Parity Ratio: %.4f",
                anomaly.anomalyScore(), anomaly.assessment(), anomaly.totalEvaluatedPixels(), anomaly.lsbZeroRatio()));
        anomalyLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " +
                (anomaly.anomalyScore() > 50 ? "#F59E0B" : "#10B981") + ";");
    }

    private BufferedImage createTestPattern(int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int r = (x * 255) / w;
                int gr = (y * 255) / h;
                int b = ((x + y) * 255) / (w + h);
                img.setRGB(x, y, (r << 16) | (gr << 8) | b);
            }
        }
        g.setColor(Color.WHITE);
        g.drawString("CipheRaz Benchmark", 20, 30);
        g.dispose();
        return img;
    }

    private void browseCoverImage() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select Lossless Cover Image (PNG, BMP)");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Lossless Images (*.png, *.bmp)", "*.png", "*.bmp"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File file = fc.showOpenDialog(getScene().getWindow());
        if (file != null) {
            try {
                BufferedImage loaded = ImageIO.read(file);
                if (loaded != null) {
                    coverImage = loaded;
                    coverImageView.setImage(SwingFXUtils.toFXImage(coverImage, null));
                    updateCapacityDisplay();
                    AppContext.getInstance().setStatusMessage("Cover image loaded: " + file.getName());
                }
            } catch (Exception ex) {
                AppContext.getInstance().setStatusMessage("Error loading image: " + ex.getMessage());
            }
        }
    }

    private void browseStegoImageForExtract() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open Stego Image (PNG, BMP)");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Lossless Images (*.png, *.bmp)", "*.png", "*.bmp"));
        File file = fc.showOpenDialog(getScene().getWindow());
        if (file != null) {
            try {
                BufferedImage loaded = ImageIO.read(file);
                if (loaded != null) {
                    currentStegoImage = loaded;
                    extractPreviewView.setImage(SwingFXUtils.toFXImage(currentStegoImage, null));
                    extractStatusLabel.setText("Loaded: " + file.getName());
                }
            } catch (Exception ex) {
                extractStatusLabel.setText("Error loading image: " + ex.getMessage());
            }
        }
    }

    private void saveStegoImage() {
        if (currentStegoImage == null) return;
        FileChooser fc = new FileChooser();
        fc.setTitle("Save Stego Image");
        fc.setInitialFileName("cipheraz_stego.png");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Image (*.png)", "*.png"));
        File file = fc.showSaveDialog(getScene().getWindow());
        if (file != null) {
            try {
                ImageIO.write(currentStegoImage, "png", file);
                AppContext.getInstance().setStatusMessage("Stego image exported to: " + file.getName());
            } catch (Exception ex) {
                AppContext.getInstance().setStatusMessage("Failed saving image: " + ex.getMessage());
            }
        }
    }
}
