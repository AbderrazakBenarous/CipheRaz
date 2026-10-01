package com.cryptolab.ui.views.cryptanalysis;

import com.cryptolab.app.AppContext;
import com.cryptolab.core.cryptanalysis.CaesarAttack;
import com.cryptolab.core.cryptanalysis.CaesarAttack.AttackCandidate;
import com.cryptolab.core.cryptanalysis.FrequencyAnalysis;
import com.cryptolab.core.model.SecurityStatus;
import com.cryptolab.ui.components.SecurityNoticeBanner;
import com.cryptolab.ui.components.SvgIcons;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;

import java.util.List;

/**
 * Interactive Cryptanalysis Laboratory Workstation.
 * Features letter frequency histograms, Index of Coincidence (IoC) calculation,
 * and automated Caesar brute-force dictionary attacks with Chi-Square ranking.
 *
 * Developed by Abderrazak BENAROUS
 */
public class CryptanalysisLabView extends ScrollPane {

    private final CaesarAttack caesarAttack = new CaesarAttack();

    private TextArea ciphertextArea;
    private BarChart<String, Number> frequencyChart;
    private Label iocLabel;
    private Label chiSquareLabel;
    private Label mostFrequentLabel;
    private Label iocExplanationLabel;
    private TableView<AttackCandidate> attackTable;
    private TextArea selectedCandidateArea;

    public CryptanalysisLabView() {
        setFitToWidth(true);
        setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-padding: 0;");

        VBox root = new VBox(20);
        root.getStyleClass().add("content-area");

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Cryptanalysis & Attack Laboratory");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #F9FAFB;");
        header.getChildren().addAll(SvgIcons.createIcon(SvgIcons.ICON_SEARCH, 24, "#F59E0B"), title);

        // Security Notice
        SecurityNoticeBanner banner = new SecurityNoticeBanner(
                SecurityStatus.CLASSICAL_BROKEN,
                "Cryptanalysis attacks exploit statistical anomalies, non-uniform character distributions, and small keyspaces to recover plaintext without knowledge of the secret key."
        );

        // Input Panel
        VBox inputCard = createInputCard();

        // Analysis Work Area (Split into Frequency Analysis and Attack Simulator)
        HBox analysisBox = new HBox(20);
        VBox freqCol = createFrequencyCard();
        VBox attackCol = createAttackCard();
        HBox.setHgrow(freqCol, Priority.ALWAYS);
        HBox.setHgrow(attackCol, Priority.ALWAYS);
        analysisBox.getChildren().addAll(freqCol, attackCol);

        root.getChildren().addAll(header, banner, inputCard, analysisBox);
        setContent(root);

        // Perform initial analysis on sample text
        runAnalysis();
    }

    private VBox createInputCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("lab-card");

        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Label cardTitle = new Label("CIPHERTEXT UNDER ANALYSIS");
        cardTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #F59E0B;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button sampleCaesarBtn = new Button("Load Caesar Sample (k=7)");
        sampleCaesarBtn.getStyleClass().add("btn-secondary");
        sampleCaesarBtn.setOnAction(e -> {
            // "DEFEND THE LAB AT ALL COSTS BEFORE THE ATTACK AT DAWN" shifted by 7
            ciphertextArea.setText("KLMLUK AOL SHI HA HSS JVZAZ ILMVYL AOL HAAHJR HA KHDU");
            runAnalysis();
        });

        Button sampleLongBtn = new Button("Load Long Corpus");
        sampleLongBtn.getStyleClass().add("btn-secondary");
        sampleLongBtn.setOnAction(e -> {
            ciphertextArea.setText(
                    "WKLV LV DQ HGXFDWLRQDO GHPRQVWUDWLRQ RI FUBSWDQDOBVLV LQ FUBPWRODE. " +
                    "FUBSWRJUDSKB DQG VWHJDQRJUDSKB DUFKLWHFWXUH GHVLJQHG IRU XQLYHUVLWB VWXGHQWV. " +
                    "IUHTXHQFB DQDOBVLV HASHFWV WKH OHWWHU H WR EH WKH PRVW FRPPRQ FKDUDFWHU."
            );
            runAnalysis();
        });

        topRow.getChildren().addAll(cardTitle, spacer, sampleCaesarBtn, sampleLongBtn);

        ciphertextArea = new TextArea("KLMLUK AOL SHI HA HSS JVZAZ ILMVYL AOL HAAHJR HA KHDU");
        ciphertextArea.setWrapText(true);
        ciphertextArea.setPrefRowCount(3);
        ciphertextArea.setStyle("-fx-control-inner-background: #0B0F19; -fx-text-fill: #F9FAFB; -fx-font-family: monospace; -fx-font-size: 13px;");

        Button analyzeBtn = new Button("Analyze Ciphertext & Calculate Frequencies");
        analyzeBtn.getStyleClass().add("btn-primary");
        analyzeBtn.setOnAction(e -> runAnalysis());

        card.getChildren().addAll(topRow, ciphertextArea, analyzeBtn);
        return card;
    }

    private VBox createFrequencyCard() {
        VBox card = new VBox(14);
        card.getStyleClass().add("lab-card");

        Label cardTitle = new Label("FREQUENCY ANALYSIS & COINCIDENCE INDEX");
        cardTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #38BDF8;");

        // Metrics Banner
        HBox metricsBox = new HBox(16);
        metricsBox.setAlignment(Pos.CENTER_LEFT);
        metricsBox.setStyle("-fx-background-color: #111827; -fx-padding: 10 14 10 14; -fx-border-color: #374151; -fx-border-radius: 8; -fx-background-radius: 8;");

        iocLabel = new Label("Index of Coincidence (IC): --");
        iocLabel.setStyle("-fx-text-fill: #38BDF8; -fx-font-weight: bold; -fx-font-size: 12px;");

        chiSquareLabel = new Label("Chi-Square (vs English): --");
        chiSquareLabel.setStyle("-fx-text-fill: #F59E0B; -fx-font-weight: bold; -fx-font-size: 12px;");

        mostFrequentLabel = new Label("Peak Letter: --");
        mostFrequentLabel.setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold; -fx-font-size: 12px;");

        metricsBox.getChildren().addAll(iocLabel, new Label("│") {{ setStyle("-fx-text-fill: #4B5563;"); }}, chiSquareLabel, new Label("│") {{ setStyle("-fx-text-fill: #4B5563;"); }}, mostFrequentLabel);

        // Chart
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Letter");
        NumberAxis yAxis = new NumberAxis(0, 20, 5);
        yAxis.setLabel("Frequency (%)");

        frequencyChart = new BarChart<>(xAxis, yAxis);
        frequencyChart.setTitle("Ciphertext vs Standard English Letter Distribution");
        frequencyChart.setAnimated(false);
        frequencyChart.setPrefHeight(260);
        frequencyChart.setStyle("-fx-background-color: transparent; -fx-text-fill: #F9FAFB;");

        // Educational Diagnostic Note
        iocExplanationLabel = new Label(
                "Diagnostic Tip: Standard English Index of Coincidence (IoC) is ~0.067. " +
                "Monoalphabetic ciphers preserve this high IoC (~0.065-0.070), while polyalphabetic ciphers (Vigenère) flatten it towards ~0.038 (random distribution)."
        );
        iocExplanationLabel.setWrapText(true);
        iocExplanationLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #94A3B8;");

        card.getChildren().addAll(cardTitle, metricsBox, frequencyChart, iocExplanationLabel);
        return card;
    }

    private VBox createAttackCard() {
        VBox card = new VBox(14);
        card.getStyleClass().add("lab-card");

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Label cardTitle = new Label("CAESAR BRUTE-FORCE ATTACK ENGINE");
        cardTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #EF4444;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button attackBtn = new Button("Execute Caesar Brute-Force");
        attackBtn.setStyle("-fx-background-color: #EF4444; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
        attackBtn.setOnAction(e -> runCaesarAttack());

        topRow.getChildren().addAll(cardTitle, spacer, attackBtn);

        // Candidates Table
        attackTable = new TableView<>();
        attackTable.setPrefHeight(200);
        attackTable.setStyle("-fx-control-inner-background: #0B0F19; -fx-text-fill: #F9FAFB; -fx-font-size: 12px;");

        TableColumn<AttackCandidate, Integer> shiftCol = new TableColumn<>("Shift (k)");
        shiftCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().shiftUsed()).asObject());
        shiftCol.setPrefWidth(65);

        TableColumn<AttackCandidate, Double> chiCol = new TableColumn<>("Chi-Square (χ²)");
        chiCol.setCellValueFactory(c -> new SimpleDoubleProperty(Math.round(c.getValue().chiSquareScore() * 100.0) / 100.0).asObject());
        chiCol.setPrefWidth(100);

        TableColumn<AttackCandidate, String> confCol = new TableColumn<>("Confidence");
        confCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().confidence()));
        confCol.setPrefWidth(180);

        TableColumn<AttackCandidate, String> previewCol = new TableColumn<>("Decrypted Candidate Preview");
        previewCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().candidateText()));
        previewCol.setPrefWidth(220);

        attackTable.getColumns().addAll(shiftCol, chiCol, confCol, previewCol);

        attackTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                selectedCandidateArea.setText(newV.candidateText());
            }
        });

        // Inspection preview
        selectedCandidateArea = new TextArea();
        selectedCandidateArea.setPromptText("Select a candidate above to view complete decrypted text...");
        selectedCandidateArea.setWrapText(true);
        selectedCandidateArea.setPrefRowCount(3);
        selectedCandidateArea.setEditable(false);
        selectedCandidateArea.setStyle("-fx-control-inner-background: #0B0F19; -fx-text-fill: #10B981; -fx-font-family: monospace;");

        Button copyCandidateBtn = new Button("Copy Plaintext");
        copyCandidateBtn.getStyleClass().add("btn-secondary");
        copyCandidateBtn.setOnAction(e -> {
            String text = selectedCandidateArea.getText();
            if (text != null && !text.isEmpty()) {
                ClipboardContent content = new ClipboardContent();
                content.putString(text);
                Clipboard.getSystemClipboard().setContent(content);
                AppContext.getInstance().setStatusMessage("Recovered candidate text copied to clipboard");
            }
        });

        card.getChildren().addAll(topRow, attackTable, new Label("Selected Candidate Inspection:") {{
            setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #9CA3AF;");
        }}, selectedCandidateArea, copyCandidateBtn);
        return card;
    }

    private void runAnalysis() {
        String text = ciphertextArea.getText();
        FrequencyAnalysis.AnalysisResult result = FrequencyAnalysis.analyze(text);

        iocLabel.setText(String.format("Index of Coincidence (IC): %.4f", result.indexOfCoincidence()));
        chiSquareLabel.setText(String.format("Chi-Square (vs English): %.2f", result.chiSquareEnglish()));
        mostFrequentLabel.setText(String.format("Peak Letter: '%c' (%d occurrences)", result.mostFrequentLetter(),
                result.totalLetters() > 0 ? result.letterCounts()[result.mostFrequentLetter() - 'A'] : 0));

        // Update Frequency Chart
        frequencyChart.getData().clear();

        XYChart.Series<String, Number> observedSeries = new XYChart.Series<>();
        observedSeries.setName("Ciphertext Actual (%)");

        XYChart.Series<String, Number> englishSeries = new XYChart.Series<>();
        englishSeries.setName("English Standard (%)");

        for (int i = 0; i < 26; i++) {
            String letter = String.valueOf((char) ('A' + i));
            observedSeries.getData().add(new XYChart.Data<>(letter, result.letterPercentages()[i]));
            englishSeries.getData().add(new XYChart.Data<>(letter, FrequencyAnalysis.ENGLISH_FREQUENCIES[i]));
        }

        frequencyChart.getData().addAll(observedSeries, englishSeries);
        AppContext.getInstance().setStatusMessage("Frequency analysis completed on " + result.totalLetters() + " alphabetic characters");
    }

    private void runCaesarAttack() {
        String ciphertext = ciphertextArea.getText();
        List<AttackCandidate> candidates = caesarAttack.executeAttack(ciphertext);
        ObservableList<AttackCandidate> data = FXCollections.observableArrayList(candidates);
        attackTable.setItems(data);

        if (!candidates.isEmpty()) {
            attackTable.getSelectionModel().select(0);
            AttackCandidate best = candidates.get(0);
            AppContext.getInstance().setStatusMessage(String.format("Attack complete. Best candidate: Shift %d (χ² = %.2f)", best.shiftUsed(), best.chiSquareScore()));
        }
    }
}
