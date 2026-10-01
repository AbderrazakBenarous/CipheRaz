package com.cryptolab.ui.views.classical;

import com.cryptolab.app.AppContext;
import com.cryptolab.app.NavigationManager;
import com.cryptolab.core.classical.*;
import com.cryptolab.core.model.CryptoResult;
import com.cryptolab.core.model.SecurityStatus;
import com.cryptolab.ui.components.SecurityNoticeBanner;
import com.cryptolab.ui.components.SvgIcons;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Interactive Classical Cryptography Laboratory Workstation.
 * Provides parameter configuration, encryption/decryption, step-by-step traces,
 * and key visualizers for historical ciphers.
 *
 * Developed by Abderrazak BENAROUS
 */
public class ClassicalLabView extends ScrollPane {

    private final NavigationManager navigationManager;

    private final CaesarCipher caesarCipher = new CaesarCipher();
    private final AtbashCipher atbashCipher = new AtbashCipher();
    private final AffineCipher affineCipher = new AffineCipher();
    private final VigenereCipher vigenereCipher = new VigenereCipher();
    private final PlayfairCipher playfairCipher = new PlayfairCipher();
    private final RailFenceCipher railFenceCipher = new RailFenceCipher();
    private final SimpleSubstitutionCipher substitutionCipher = new SimpleSubstitutionCipher();

    private ComboBox<String> algorithmCombo;
    private TextArea inputArea;
    private TextArea outputArea;
    private Label durationLabel;
    private ListView<String> traceListView;
    private Label alphabetMapLabel;
    private Label explanationLabel;

    // Parameter Controls
    private VBox parameterBox;
    private Slider caesarSlider;
    private Label caesarSliderVal;
    private ComboBox<Integer> affineACombo;
    private Spinner<Integer> affineBSpinner;
    private TextField vigenereKeyField;
    private TextField playfairKeyField;
    private GridPane playfairMatrixGrid;
    private Spinner<Integer> railFenceSpinner;
    private TextField substitutionKeyField;

    public ClassicalLabView(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;

        setFitToWidth(true);
        setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-padding: 0;");

        VBox root = new VBox(20);
        root.getStyleClass().add("content-area");

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Classical Cryptography Laboratory");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #F9FAFB;");
        header.getChildren().addAll(SvgIcons.createIcon(SvgIcons.ICON_SHIELD, 24, "#38BDF8"), title);

        // Security Notice
        SecurityNoticeBanner securityBanner = new SecurityNoticeBanner(
                SecurityStatus.CLASSICAL_BROKEN,
                "Classical ciphers are vulnerable to frequency analysis and exhaustive brute force. They are studied strictly to understand the historical evolution of cryptography."
        );

        // Algorithm Selector Bar
        HBox selectorBar = createAlgorithmSelector();

        // Main Workstation Split (Left: Input & Parameters, Right: Output & Inspector)
        HBox workArea = new HBox(20);
        VBox leftCol = createInputAndParamsPanel();
        VBox rightCol = createOutputAndInspectorPanel();
        HBox.setHgrow(leftCol, Priority.ALWAYS);
        HBox.setHgrow(rightCol, Priority.ALWAYS);
        workArea.getChildren().addAll(leftCol, rightCol);

        root.getChildren().addAll(header, securityBanner, selectorBar, workArea);
        setContent(root);

        // Trigger initial state
        updateParameterControls("Caesar Cipher");
    }

    private HBox createAlgorithmSelector() {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: #111827; -fx-padding: 12 16 12 16; -fx-background-radius: 8; -fx-border-color: #374151; -fx-border-radius: 8;");

        Label label = new Label("Select Cipher Primitive:");
        label.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #9CA3AF;");

        algorithmCombo = new ComboBox<>();
        algorithmCombo.getItems().addAll(
                "Caesar Cipher",
                "Atbash Cipher",
                "Affine Cipher",
                "Vigenère Cipher",
                "Playfair Cipher",
                "Rail Fence Cipher",
                "Simple Substitution Cipher"
        );
        algorithmCombo.setValue("Caesar Cipher");
        algorithmCombo.setStyle("-fx-background-color: #1F2937; -fx-text-fill: #F9FAFB; -fx-font-size: 13px; -fx-pref-width: 260;");
        algorithmCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateParameterControls(newVal);
            }
        });

        bar.getChildren().addAll(label, algorithmCombo);
        return bar;
    }

    private VBox createInputAndParamsPanel() {
        VBox panel = new VBox(14);
        panel.getStyleClass().add("lab-card");

        Label panelTitle = new Label("PLAINTEXT / CIPHERTEXT INPUT");
        panelTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #38BDF8;");

        inputArea = new TextArea("ATTACK AT DAWN ON THE NORTHERN CORRIDOR");
        inputArea.setPromptText("Enter text to encrypt or decrypt...");
        inputArea.setWrapText(true);
        inputArea.setPrefRowCount(5);
        inputArea.setStyle("-fx-control-inner-background: #0B0F19; -fx-text-fill: #F9FAFB; -fx-font-family: monospace; -fx-font-size: 13px;");

        HBox sampleButtons = new HBox(8);
        Button sampleBtn = new Button("Load Sample");
        sampleBtn.getStyleClass().add("btn-secondary");
        sampleBtn.setOnAction(e -> inputArea.setText("THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG"));

        Button clearBtn = new Button("Clear");
        clearBtn.getStyleClass().add("btn-secondary");
        clearBtn.setOnAction(e -> {
            inputArea.clear();
            outputArea.clear();
            traceListView.getItems().clear();
        });

        sampleButtons.getChildren().addAll(sampleBtn, clearBtn);

        // Parameters container
        parameterBox = new VBox(10);
        parameterBox.setStyle("-fx-background-color: #111827; -fx-padding: 12; -fx-background-radius: 8; -fx-border-color: #374151; -fx-border-radius: 8;");

        // Action Buttons
        HBox actionBox = new HBox(12);
        actionBox.setAlignment(Pos.CENTER_LEFT);

        Button encryptBtn = new Button("Encrypt");
        encryptBtn.getStyleClass().add("btn-primary");
        encryptBtn.setOnAction(e -> executeOperation(true));

        Button decryptBtn = new Button("Decrypt");
        decryptBtn.getStyleClass().add("btn-secondary");
        decryptBtn.setOnAction(e -> executeOperation(false));

        Button sendAnalysisBtn = new Button("Analyze in Cryptanalysis Lab");
        sendAnalysisBtn.setStyle("-fx-background-color: rgba(245, 158, 11, 0.15); -fx-text-fill: #F59E0B; -fx-border-color: #F59E0B; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
        sendAnalysisBtn.setOnAction(e -> {
            // Send current output to cryptanalysis view
            navigationManager.navigateTo(NavigationManager.ViewId.CRYPTANALYSIS);
        });

        actionBox.getChildren().addAll(encryptBtn, decryptBtn, sendAnalysisBtn);

        panel.getChildren().addAll(panelTitle, inputArea, sampleButtons, parameterBox, actionBox);
        return panel;
    }

    private VBox createOutputAndInspectorPanel() {
        VBox panel = new VBox(14);
        panel.getStyleClass().add("lab-card");

        HBox outHeader = new HBox(10);
        outHeader.setAlignment(Pos.CENTER_LEFT);
        Label panelTitle = new Label("RESULT & INTERNAL STATE TRACER");
        panelTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #818CF8;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        durationLabel = new Label("Execution: -- ms");
        durationLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #94A3B8;");

        outHeader.getChildren().addAll(panelTitle, spacer, durationLabel);

        outputArea = new TextArea();
        outputArea.setPromptText("Transformed ciphertext / plaintext will appear here...");
        outputArea.setWrapText(true);
        outputArea.setEditable(false);
        outputArea.setPrefRowCount(4);
        outputArea.setStyle("-fx-control-inner-background: #0B0F19; -fx-text-fill: #38BDF8; -fx-font-family: monospace; -fx-font-size: 13px;");

        Button copyBtn = new Button("Copy to Clipboard");
        copyBtn.getStyleClass().add("btn-secondary");
        copyBtn.setOnAction(e -> {
            String text = outputArea.getText();
            if (text != null && !text.isEmpty()) {
                ClipboardContent content = new ClipboardContent();
                content.putString(text);
                Clipboard.getSystemClipboard().setContent(content);
                AppContext.getInstance().setStatusMessage("Output copied to clipboard");
            }
        });

        // Tabs for Inspector
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-tab-min-height: 28;");

        // Tab 1: Step Traces
        Tab traceTab = new Tab("Step-by-Step Trace");
        traceTab.setClosable(false);
        traceListView = new ListView<>();
        traceListView.setStyle("-fx-control-inner-background: #0B0F19; -fx-text-fill: #9CA3AF; -fx-font-family: monospace; -fx-font-size: 11px;");
        traceTab.setContent(traceListView);

        // Tab 2: Alphabet Mapping Visualizer
        Tab mapTab = new Tab("Alphabet Mapping");
        mapTab.setClosable(false);
        VBox mapBox = new VBox(8);
        mapBox.setStyle("-fx-background-color: #0B0F19; -fx-padding: 12;");
        alphabetMapLabel = new Label("Run cipher to generate mapping visualization.");
        alphabetMapLabel.setStyle("-fx-font-family: monospace; -fx-font-size: 12px; -fx-text-fill: #38BDF8;");
        mapBox.getChildren().add(alphabetMapLabel);
        mapTab.setContent(mapBox);

        // Tab 3: Explanation & Security Notes
        Tab explanationTab = new Tab("Educational Notes");
        explanationTab.setClosable(false);
        VBox expBox = new VBox(8);
        expBox.setStyle("-fx-background-color: #0B0F19; -fx-padding: 12;");
        explanationLabel = new Label("Select an algorithm and run an experiment to view theoretical context.");
        explanationLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #CBD5E1; -fx-wrap-text: true;");
        expBox.getChildren().add(explanationLabel);
        explanationTab.setContent(expBox);

        tabPane.getTabs().addAll(traceTab, mapTab, explanationTab);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        panel.getChildren().addAll(outHeader, outputArea, copyBtn, tabPane);
        return panel;
    }

    private void updateParameterControls(String algorithmName) {
        parameterBox.getChildren().clear();

        Label label = new Label("Cipher Parameters:");
        label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #9CA3AF;");
        parameterBox.getChildren().add(label);

        switch (algorithmName) {
            case "Caesar Cipher" -> {
                caesarSlider = new Slider(0, 25, 3);
                caesarSlider.setBlockIncrement(1);
                caesarSlider.setMajorTickUnit(5);
                caesarSlider.setMinorTickCount(0);
                caesarSlider.setSnapToTicks(true);
                caesarSlider.setShowTickMarks(true);
                caesarSlider.setShowTickLabels(true);

                caesarSliderVal = new Label("Shift (k): 3");
                caesarSliderVal.setStyle("-fx-font-weight: bold; -fx-text-fill: #38BDF8;");

                caesarSlider.valueProperty().addListener((obs, o, n) -> {
                    int val = n.intValue();
                    caesarSliderVal.setText("Shift (k): " + val);
                    updateCaesarAlphabetMap(val);
                });

                updateCaesarAlphabetMap(3);
                parameterBox.getChildren().addAll(caesarSliderVal, caesarSlider);
            }
            case "Atbash Cipher" -> {
                Label atbashNote = new Label("Atbash is an involution without key parameters. Alphabet is mirrored: A<->Z, B<->Y.");
                atbashNote.setStyle("-fx-font-size: 12px; -fx-text-fill: #CBD5E1;");
                alphabetMapLabel.setText(
                        "Plain:   A B C D E F G H I J K L M N O P Q R S T U V W X Y Z\n" +
                        "Cipher:  Z Y X W V U T S R Q P O N M L K J I H G F E D C B A"
                );
                parameterBox.getChildren().add(atbashNote);
            }
            case "Affine Cipher" -> {
                HBox affineBox = new HBox(12);
                affineBox.setAlignment(Pos.CENTER_LEFT);

                Label aLabel = new Label("Multiplier a (coprime to 26):");
                aLabel.setStyle("-fx-text-fill: #CBD5E1; -fx-font-size: 12px;");
                affineACombo = new ComboBox<>();
                affineACombo.getItems().addAll(AffineCipher.VALID_A_VALUES);
                affineACombo.setValue(5);
                affineACombo.setStyle("-fx-background-color: #1F2937; -fx-text-fill: #F9FAFB;");

                Label bLabel = new Label("Shift b:");
                bLabel.setStyle("-fx-text-fill: #CBD5E1; -fx-font-size: 12px;");
                affineBSpinner = new Spinner<>(0, 25, 8);
                affineBSpinner.setStyle("-fx-background-color: #1F2937;");
                affineBSpinner.setPrefWidth(80);

                affineBox.getChildren().addAll(aLabel, affineACombo, bLabel, affineBSpinner);
                parameterBox.getChildren().add(affineBox);
            }
            case "Vigenère Cipher" -> {
                HBox vigBox = new HBox(10);
                vigBox.setAlignment(Pos.CENTER_LEFT);
                Label kLabel = new Label("Keyword:");
                kLabel.setStyle("-fx-text-fill: #CBD5E1; -fx-font-size: 12px;");
                vigenereKeyField = new TextField("CIPHER");
                vigenereKeyField.setStyle("-fx-background-color: #0B0F19; -fx-text-fill: #F9FAFB; -fx-font-family: monospace;");
                HBox.setHgrow(vigenereKeyField, Priority.ALWAYS);
                vigBox.getChildren().addAll(kLabel, vigenereKeyField);
                parameterBox.getChildren().add(vigBox);
            }
            case "Playfair Cipher" -> {
                VBox pfBox = new VBox(8);
                HBox keyRow = new HBox(10);
                keyRow.setAlignment(Pos.CENTER_LEFT);
                Label kLabel = new Label("Playfair Keyword:");
                kLabel.setStyle("-fx-text-fill: #CBD5E1; -fx-font-size: 12px;");
                playfairKeyField = new TextField("MONARCHY");
                playfairKeyField.setStyle("-fx-background-color: #0B0F19; -fx-text-fill: #F9FAFB; -fx-font-family: monospace;");
                HBox.setHgrow(playfairKeyField, Priority.ALWAYS);

                playfairKeyField.textProperty().addListener((obs, o, n) -> renderPlayfairGrid(n));

                keyRow.getChildren().addAll(kLabel, playfairKeyField);

                playfairMatrixGrid = new GridPane();
                playfairMatrixGrid.setHgap(6);
                playfairMatrixGrid.setVgap(6);
                renderPlayfairGrid("MONARCHY");

                pfBox.getChildren().addAll(keyRow, new Label("5x5 Matrix Preview (I/J merged):") {{
                    setStyle("-fx-font-size: 11px; -fx-text-fill: #9CA3AF;");
                }}, playfairMatrixGrid);

                parameterBox.getChildren().add(pfBox);
            }
            case "Rail Fence Cipher" -> {
                HBox rfBox = new HBox(10);
                rfBox.setAlignment(Pos.CENTER_LEFT);
                Label rLabel = new Label("Number of Rails (zigzag depth):");
                rLabel.setStyle("-fx-text-fill: #CBD5E1; -fx-font-size: 12px;");
                railFenceSpinner = new Spinner<>(2, 10, 3);
                railFenceSpinner.setPrefWidth(80);
                rfBox.getChildren().addAll(rLabel, railFenceSpinner);
                parameterBox.getChildren().add(rfBox);
            }
            case "Simple Substitution Cipher" -> {
                VBox subBox = new VBox(8);
                HBox keyRow = new HBox(8);
                keyRow.setAlignment(Pos.CENTER_LEFT);

                substitutionKeyField = new TextField("QWERTYUIOPASDFGHJKLZXCVBNM");
                substitutionKeyField.setStyle("-fx-background-color: #0B0F19; -fx-text-fill: #F9FAFB; -fx-font-family: monospace;");
                HBox.setHgrow(substitutionKeyField, Priority.ALWAYS);

                Button shuffleBtn = new Button("Shuffle Key");
                shuffleBtn.getStyleClass().add("btn-secondary");
                shuffleBtn.setOnAction(e -> {
                    List<Character> letters = new ArrayList<>();
                    for (char c = 'A'; c <= 'Z'; c++) letters.add(c);
                    Collections.shuffle(letters);
                    StringBuilder sb = new StringBuilder();
                    for (char c : letters) sb.append(c);
                    substitutionKeyField.setText(sb.toString());
                });

                keyRow.getChildren().addAll(substitutionKeyField, shuffleBtn);
                subBox.getChildren().addAll(new Label("26-Letter Substitution Alphabet (A-Z):") {{
                    setStyle("-fx-font-size: 11px; -fx-text-fill: #9CA3AF;");
                }}, keyRow);
                parameterBox.getChildren().add(subBox);
            }
        }
    }

    private void renderPlayfairGrid(String keyword) {
        if (playfairMatrixGrid == null) return;
        playfairMatrixGrid.getChildren().clear();
        char[][] matrix = playfairCipher.generateKeyMatrix(keyword);
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                Label cell = new Label(String.valueOf(matrix[r][c]));
                cell.setStyle("-fx-background-color: #1F2937; -fx-text-fill: #38BDF8; -fx-font-weight: bold; -fx-padding: 4 8 4 8; -fx-border-color: #374151; -fx-border-radius: 4; -fx-background-radius: 4; -fx-min-width: 28; -fx-alignment: CENTER;");
                playfairMatrixGrid.add(cell, c, r);
            }
        }
    }

    private void updateCaesarAlphabetMap(int shift) {
        StringBuilder plain = new StringBuilder("Plain:   ");
        StringBuilder cipher = new StringBuilder("Cipher:  ");
        for (int i = 0; i < 26; i++) {
            char p = (char) ('A' + i);
            char c = (char) ('A' + ((i + shift) % 26));
            plain.append(p).append(" ");
            cipher.append(c).append(" ");
        }
        alphabetMapLabel.setText(plain + "\n" + cipher);
    }

    private void executeOperation(boolean encrypt) {
        String input = inputArea.getText();
        String selected = algorithmCombo.getValue();
        CryptoResult result;

        switch (selected) {
            case "Caesar Cipher" -> {
                int shift = (int) caesarSlider.getValue();
                result = encrypt ? caesarCipher.encrypt(input, shift) : caesarCipher.decrypt(input, shift);
            }
            case "Atbash Cipher" -> result = atbashCipher.process(input);
            case "Affine Cipher" -> {
                int a = affineACombo.getValue() != null ? affineACombo.getValue() : 5;
                int b = affineBSpinner.getValue();
                result = encrypt ? affineCipher.encrypt(input, a, b) : affineCipher.decrypt(input, a, b);
            }
            case "Vigenère Cipher" -> {
                String key = vigenereKeyField.getText();
                result = encrypt ? vigenereCipher.encrypt(input, key) : vigenereCipher.decrypt(input, key);
            }
            case "Playfair Cipher" -> {
                String key = playfairKeyField.getText();
                result = encrypt ? playfairCipher.encrypt(input, key) : playfairCipher.decrypt(input, key);
            }
            case "Rail Fence Cipher" -> {
                int rails = railFenceSpinner.getValue();
                result = encrypt ? railFenceCipher.encrypt(input, rails) : railFenceCipher.decrypt(input, rails);
            }
            case "Simple Substitution Cipher" -> {
                String key = substitutionKeyField.getText();
                result = encrypt ? substitutionCipher.encrypt(input, key) : substitutionCipher.decrypt(input, key);
            }
            default -> result = CryptoResult.failure("Unknown algorithm");
        }

        if (result.success()) {
            outputArea.setText(result.outputText());
            durationLabel.setText(String.format("Execution: %.2f ms", result.executionDurationNanos() / 1_000_000.0));
            traceListView.getItems().setAll(result.stepTraces());
            explanationLabel.setText(result.educationalExplanation());
            AppContext.getInstance().setStatusMessage(selected + " " + (encrypt ? "encryption" : "decryption") + " succeeded");
        } else {
            outputArea.setText("ERROR: " + result.errorMessage());
            durationLabel.setText("Execution: Failed");
            traceListView.getItems().clear();
            explanationLabel.setText("Error encountered: " + result.errorMessage());
            AppContext.getInstance().setStatusMessage("Operation failed: " + result.errorMessage());
        }
    }
}
